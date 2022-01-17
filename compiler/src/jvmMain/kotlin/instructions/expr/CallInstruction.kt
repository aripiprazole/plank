package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildBitcast
import com.gabrielleeg1.plank.compiler.buildCall
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.compiler.unsafeCast
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.LoadInstruction

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      when (val functionType = type.parameters[index].cast<FunctionType>()) {
        is FunctionType -> {
          val closureType = functionType.copy(isClosure = true).toType().bind().let {
            context.getPointerType(it).unwrap()
          }

          buildBitcast(expr.toInstruction().codegen().bind(), closureType)
        }
        else -> expr.toInstruction().codegen().bind()
      }
    }

    when (descriptor.callee.type.isClosure) {
      true -> {
        var closure = descriptor.callee.toInstruction().codegen().bind()

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
        val callee = when (val callee = descriptor.callee.toInstruction().codegen().bind()) {
          is Function -> callee
          is LoadInstruction -> callee.unsafeCast()
          is AllocaInstruction -> buildLoad(callee).unsafeCast()
          else -> unresolvedFunctionError(descriptor.callee).left().bind<Function>()
        }

        buildCall(callee, arguments)
      }
    }
  }
}
