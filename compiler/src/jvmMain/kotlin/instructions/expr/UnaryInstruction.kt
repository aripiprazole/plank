package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildFNeg
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Unary.Operation
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class UnaryInstruction(private val descriptor: Expr.Unary) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Neg -> buildFNeg(rhs, "neg.tmp")
        Operation.Bang -> buildFNeg(rhs, "neg.tmp")
      }
    )
  }
}
