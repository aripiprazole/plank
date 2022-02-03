package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.alloca
import com.gabrielleeg1.plank.compiler.castClosure
import com.gabrielleeg1.plank.compiler.element.CurryFunctionInst
import com.gabrielleeg1.plank.compiler.getField
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.PointerType
import org.plank.llvm4k.ir.Value

class CallInst(private val descriptor: TypedCallExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val type = descriptor.callee.type.cast<FunctionType>()!!

    val arguments = descriptor.arguments.mapIndexed { index, expr ->
      val functionType = expr.type.cast<FunctionType>()

      when {
        functionType != null && functionType.isNested -> {
          castClosure(expr.codegen(), type.parameters.values.elementAt(index).typegen())
        }
        functionType != null && !functionType.isPartialApplied -> { // FIXME: access function with lazy
          val name = "_Zclosure.wrap.(${descriptor.callee.type})$$index"

          val function = addFunction(
            CurryFunctionInst(
              name = name,
              mangled = name,
              type = functionType,
              returnType = functionType.actualReturnType,
              realParameters = functionType.realParameters,
              references = functionType.references,
              nested = true,
              generate = {
                createRet(callClosure(expr.codegen(), *arguments.values.toTypedArray()))
              }
            )
          )

          castClosure(function, type.parameters.values.elementAt(index).typegen())
        }
        else -> expr.codegen()
      }
    }

    val callee = descriptor.callee.codegen()

    return callClosure(callee, *arguments.toTypedArray())
  }
}

fun CodegenContext.callClosure(value: Value, vararg arguments: Value, name: String? = ""): Value {
  var closure = value

  if (closure.type !is PointerType) {
    closure = alloca(closure)
  }

  val prefix = if (name.isNullOrBlank()) "" else "$name."

  val function = getField(closure, 0, if (name != null) "${prefix}fn" else null)
    .let(::createLoad)
    .let { Function(it.ref) }

  val environment = getField(closure, 1, if (name != null) "${prefix}env" else null)
    .let(::createLoad)

  return createCall(function, environment, *arguments)
}
