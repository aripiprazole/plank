package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.compiler.unsafeCast
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.LoadInstruction
import org.llvm4j.llvm4j.Value

fun CompilerContext.callClosure(value: Value, vararg arguments: Value, name: String? = ""): Value {
  var closure = value

  if (!closure.getType().isPointerType()) {
    closure = alloca(closure)
  }

  val prefix = if (name.isNullOrBlank()) "" else "$name."

  val function = getField(closure, 0, if (name != null) "${prefix}fn" else null)
    .let(::buildLoad)
    .unsafeCast<Function>()

  val environment = getField(closure, 1, if (name != null) "${prefix}env" else null)
    .let(::buildLoad)

  return buildCall(function, environment, *arguments)
}

fun CompilerContext.callee(descriptor: TypedExpr): Function =
  when (val callee = descriptor.codegen()) {
    is Function -> callee
    is LoadInstruction -> callee.unsafeCast()
    is AllocaInstruction -> buildLoad(callee).unsafeCast()
    else -> unresolvedFunctionError(descriptor)
  }
