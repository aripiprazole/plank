package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildFDiv
import com.gabrielleeg1.plank.compiler.buildIAdd
import com.gabrielleeg1.plank.compiler.buildIMul
import com.gabrielleeg1.plank.compiler.buildISub
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Expr.Binary.Operation
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class BinaryInstruction(val descriptor: Expr.Binary) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val lhs = !descriptor.lhs.toInstruction().codegen()
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Add -> buildIAdd(lhs, rhs, "add.tmp")
        Operation.Sub -> buildISub(lhs, rhs, "sub.tmp")
        Operation.Mul -> buildIMul(lhs, rhs, "mul.tmp")
        Operation.Div -> buildFDiv(!lhs.toFloat(), !rhs.toFloat(), "div.tmp")
      }
    )
  }
}
