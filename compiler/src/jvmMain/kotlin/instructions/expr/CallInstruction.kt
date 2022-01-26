package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.callClosure
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRCurried
import org.llvm4j.llvm4j.Value

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      val functionType = expr.type.cast<FunctionType>()
      when {
        functionType != null && functionType.isClosure -> {
          buildBitcast(expr.codegen(), type.parameters[index].typegen())
        }
        functionType != null && !functionType.isClosure -> { // FIXME: access function with lazy
          val name = "_Zclosure.wrap.(${descriptor.callee.type})$$index"
          val f = addFunction(
            IRCurried(
              name = name,
              mangledName = name,
              type = functionType,
              returnType = functionType.actualReturnType,
              realParameters = functionType.realParameters,
              variableReferences = functionType.references,
              nested = true,
              generateBody = {
                val value = callClosure(expr.codegen(), *parameters.values.toTypedArray())

                buildReturn(value)
              }
            )
          )

          buildBitcast(f, type.parameters[index].typegen())
        }
        else -> expr.codegen()
      }
    }

    val callee = descriptor.callee.codegen()

    return callClosure(callee, *arguments.toTypedArray())
  }
}
