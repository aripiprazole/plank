package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr

class GroupInstruction(private val descriptor: Expr.Group) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult {
    return descriptor.expr.toInstruction().codegen()
  }
}
