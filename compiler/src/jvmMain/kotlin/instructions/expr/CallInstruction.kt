package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
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
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addIrClosure
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.compiler.unsafeCast
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.LoadInstruction
import org.llvm4j.llvm4j.Value

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      when (val functionType = type.parameters[index].cast<FunctionType>()) {
        is FunctionType -> when (expr.type.isClosure) {
          false -> {
            val closure =
              addIrClosure("Closure_${hashCode()}_$index", expr.type as FunctionType) { arguments ->
                either.eager<CodegenViolation, Unit> {
                  val value = buildCall(callee(expr), arguments)

                  if (functionType.actualReturnType == UnitType) {
                    buildReturn()
                  } else {
                    buildReturn(value)
                  }
                }
              }.accessIn(this@codegen)

            val closureType = functionType.copy(isClosure = true).typegen()
              .let { pointerType(it) }

            buildBitcast(closure, closureType)
          }
          true -> {
            val closureType = functionType.copy(isClosure = true).typegen()
              .let { pointerType(it) }

            buildBitcast(alloca(expr.codegen()), closureType)
          }
        }
        else -> expr.codegen()
      }
    }

    return when (descriptor.callee.type.isClosure) {
      true -> {
        var closure = descriptor.callee.codegen()

        if (!closure.getType().isPointerType()) {
          closure = alloca(closure)
        }

        val function = getField(closure, 0, "Closure.Function")
          .let(::buildLoad)
          .unsafeCast<Function>()

        val environment = getField(closure, 1, "Closure.Environment").let(::buildLoad)

        buildCall(function, environment, *arguments.toTypedArray())
      }
      false -> {
        buildCall(callee(descriptor.callee), arguments)
      }
    }
  }

  companion object {
    fun CompilerContext.callee(descriptor: TypedExpr): Function =
      when (val callee = descriptor.codegen()) {
        is Function -> callee
        is LoadInstruction -> callee.unsafeCast()
        is AllocaInstruction -> buildLoad(callee).unsafeCast()
        else -> unresolvedFunctionError(descriptor)
      }
  }
}
