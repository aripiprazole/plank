package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.callClosure
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      when (val functionType = type.parameters[index].cast<FunctionType>()) {
        is FunctionType -> {
          val closureType = functionType.copy().typegen()

          buildBitcast(expr.codegen(), closureType)
        }
        else -> expr.codegen()
      }
    }

    return callClosure(descriptor.callee.codegen(), *arguments.toTypedArray())
  }
}
