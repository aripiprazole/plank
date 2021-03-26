package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class ValueInstruction(private val descriptor: Expr.Value) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val value = context.map(descriptor.expr).codegen(context)
      ?: return context.report("value is null", descriptor)

    return context.builder.buildLoad(value, Some("valuetmp"))
  }
}
