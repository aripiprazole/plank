package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.unsafePointerType
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addIrClosure
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.compiler.unsafeCast
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.LoadInstruction

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      when (val functionType = type.parameters[index].cast<FunctionType>()) {
        is FunctionType -> when (expr.type.isClosure) {
          false -> {
            val closure =
              addIrClosure("Closure_${hashCode()}_$index", expr.type as FunctionType) { arguments ->
                either.eager<CodegenViolation, Unit> {
                  val value = buildCall(callee(expr).bind(), arguments)

                  if (functionType.actualReturnType == UnitType) {
                    buildReturn()
                  } else {
                    buildReturn(value)
                  }
                }
              }.bind().accessIn(this@codegen)

            val closureType = functionType.copy(isClosure = true).convertType()
              .bind().let { unsafePointerType(it) }

            buildBitcast(closure, closureType)
          }
          true -> {
            val closureType = functionType.copy(isClosure = true).convertType()
              .bind().let { unsafePointerType(it) }

            buildBitcast(alloca(expr.codegen().bind()), closureType)
          }
        }
        else -> expr.codegen().bind()
      }
    }

    when (descriptor.callee.type.isClosure) {
      true -> {
        var closure = descriptor.callee.codegen().bind()

        if (!closure.getType().isPointerType()) {
          closure = alloca(closure)
        }

        val function = getField(closure, 0, "Closure.Function")
          .map(::buildLoad).bind()
          .unsafeCast<Function>()

        val environment = getField(closure, 1, "Closure.Environment")
          .map(::buildLoad).bind()

        buildCall(function, environment, *arguments.toTypedArray())
      }
      false -> {
        buildCall(callee(descriptor.callee).bind(), arguments)
      }
    }
  }

  companion object {
    fun CompilerContext.callee(descriptor: TypedExpr): Either<CodegenViolation, Function> =
      either.eager {
        when (val callee = descriptor.codegen().bind()) {
          is Function -> callee
          is LoadInstruction -> callee.unsafeCast()
          is AllocaInstruction -> buildLoad(callee).unsafeCast()
          else -> unresolvedFunctionError(descriptor).left().bind<Function>()
        }
      }
  }
}
