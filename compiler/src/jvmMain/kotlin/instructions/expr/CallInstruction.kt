package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.Value

class CallInstruction(private val descriptor: Expr.Call) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> {
        val name = callee.name.text

        context.findFunction(name)
      }
      is Expr.Get -> {
        val name = callee.member.text

        context.findFunction(name)
      }
      else -> context.report("unsupported function", descriptor)
    } ?: return context.report("callee is null", descriptor)

    return callee.call(context, descriptor.arguments)
  }
}
