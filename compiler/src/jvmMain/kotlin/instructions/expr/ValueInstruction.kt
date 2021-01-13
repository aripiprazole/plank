package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class ValueInstruction(private val descriptor: Expr.Value) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val value = context.map(descriptor.expr).codegen(context)
      ?: return context.report("value is null", descriptor)

    val type = context.map(context.binding.visit(descriptor))
      ?: return context.report("type is null", descriptor)

    return context.builder.createLoad(type, value, "valuetmp")
  }
}
