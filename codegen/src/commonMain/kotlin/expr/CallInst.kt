package org.plank.codegen.expr

import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.infer.FunTy
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.getField
import org.plank.codegen.unsafeFunction
import org.plank.llvm4k.ir.PointerType
import org.plank.llvm4k.ir.Value

class CallInst(private val descriptor: TypedCallExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val ty = descriptor.callee.ty as FunTy
    val callee = descriptor.callee.codegen()

    val argument = when (descriptor.argument.ty) {
      is FunTy -> castClosure(descriptor.argument.codegen(), ty.parameterTy.typegen())
      else -> descriptor.argument.codegen()
    }

    return callClosure(callee, argument)
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
    .let(::unsafeFunction)

  val environment = getField(closure, 1, if (name != null) "${prefix}env" else null)
    .let(::createLoad)

  return createCall(function, environment, *arguments)
}
