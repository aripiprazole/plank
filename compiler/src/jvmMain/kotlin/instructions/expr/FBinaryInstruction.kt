package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildFAdd
import com.lorenzoog.plank.compiler.buildFDiv
import com.lorenzoog.plank.compiler.buildFMul
import com.lorenzoog.plank.compiler.buildFSub
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

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
