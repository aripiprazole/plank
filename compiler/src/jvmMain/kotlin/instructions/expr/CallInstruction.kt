package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import org.llvm4j.llvm4j.Value

class CallInstruction(private val descriptor: Expr.Call) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> {
        val name = callee.name.text ?: return context.report("name is null", descriptor)

        context.findFunction(name)
      }
      is Expr.Get -> {
        val name = callee.member.text ?: return context.report("member is null", descriptor)

        context.findFunction(name)
      }
      else -> context.report("unsupported function", descriptor)
    } ?: return context.report("callee is null", descriptor)

    return callee.call(context, descriptor.arguments)
  }
}
