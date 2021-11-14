package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildFNeg
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Expr.Unary.Operation
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

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
