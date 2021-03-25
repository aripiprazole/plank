package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Expr.Unary.Operation
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class UnaryInstruction(private val descriptor: Expr.Unary) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Neg -> context.builder.buildFloatNeg(rhs, Some("negtmp"))
      Operation.Bang -> context.builder.buildFloatNeg(rhs, Some("nottmp"))
    }
  }
}
