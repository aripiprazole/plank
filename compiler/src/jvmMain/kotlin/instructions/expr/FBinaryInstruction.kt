package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildFAdd
import com.gabrielleeg1.plank.compiler.buildFDiv
import com.gabrielleeg1.plank.compiler.buildFMul
import com.gabrielleeg1.plank.compiler.buildFSub
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Expr.Binary.Operation
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class FBinaryInstruction(val descriptor: Expr.Binary) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val lhs = !descriptor.lhs.toInstruction().codegen().bind().toFloat()
    val rhs = !descriptor.rhs.toInstruction().codegen().bind().toFloat()

    Right(
      when (descriptor.op) {
        Operation.Sub -> buildFSub(lhs, rhs, "sub.tmp")
        Operation.Mul -> buildFMul(lhs, rhs, "mul.tmp")
        Operation.Div -> buildFDiv(lhs, rhs, "div.tmp")
        Operation.Add -> buildFAdd(lhs, rhs, "add.tmp")
      }
    )
  }
}
