package org.plank.compiler.expr

import org.plank.analyzer.FunctionType
import org.plank.analyzer.element.TypedCallExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.alloca
import org.plank.compiler.castClosure
import org.plank.compiler.element.CurryFunctionInst
import org.plank.compiler.getField
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

fun CodegenContext.callClosure(value: Value, vararg arguments: Value, name: String? = null): Value {
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
