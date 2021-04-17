package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildFDiv
import com.lorenzoog.plank.compiler.buildIAdd
import com.lorenzoog.plank.compiler.buildIMul
import com.lorenzoog.plank.compiler.buildISub
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

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
