package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildFNeg
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Unary.Operation
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class UnaryInstruction(private val descriptor: Expr.Unary) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Neg -> buildFNeg(rhs, "neg.tmp")
        Operation.Bang -> buildFNeg(rhs, "neg.tmp")
      }
    )
  }
}
