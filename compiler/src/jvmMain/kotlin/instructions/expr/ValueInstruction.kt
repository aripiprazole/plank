package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ValueInstruction(private val descriptor: Expr.Value) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val pointer = !descriptor.expr.toInstruction().codegen()

    Right(buildLoad(pointer, "value.tmp"))
  }
}
