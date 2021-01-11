package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class GroupInstruction(private val descriptor: Expr.Group) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return context.map(descriptor.expr).codegen(context)
  }
}
