package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.Value

class GroupInstruction(private val descriptor: Expr.Group) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return context.map(descriptor.expr).codegen(context)
  }
}
