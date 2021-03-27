package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildFAdd
import com.lorenzoog.plank.compiler.buildFDiv
import com.lorenzoog.plank.compiler.buildFMul
import com.lorenzoog.plank.compiler.buildFSub
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class FBinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val lhs = descriptor.lhs.toInstruction().codegen().bind().toFloat()
    val rhs = descriptor.rhs.toInstruction().codegen().bind().toFloat()

    Right(
      when (descriptor.op) {
        Operation.Sub -> buildFSub(rhs, lhs, "sub.tmp")
        Operation.Mul -> buildFMul(lhs, rhs, "mul.tmp")
        Operation.Div -> buildFDiv(lhs, rhs, "div.tmp")
        Operation.Add -> buildFAdd(lhs, rhs, "add.tmp")
        Operation.Concat -> return Left("The could should never reach here.")
      }
    )
  }
}
