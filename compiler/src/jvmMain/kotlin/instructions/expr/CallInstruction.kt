package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.callClosure
import com.gabrielleeg1.plank.compiler.builder.callee
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addIrClosure
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
                val value = buildCall(callee(expr), arguments)

                if (functionType.actualReturnType == UnitType) {
                  buildReturn()
                } else {
                  buildReturn(value)
                }
              }.accessIn(this@codegen)

            val closureType = functionType.copy(isClosure = true).typegen()

            buildBitcast(closure, closureType)
          }
          true -> {
            val closureType = functionType.copy(isClosure = true).typegen()

            buildBitcast(alloca(expr.codegen()), closureType)
          }
        }
        else -> expr.codegen()
      }
    }

    return callClosure(descriptor.callee.codegen(), *arguments.toTypedArray())
  }
}
