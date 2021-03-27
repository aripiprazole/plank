package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildCall
import com.lorenzoog.plank.compiler.buildFDiv
import com.lorenzoog.plank.compiler.buildIAdd
import com.lorenzoog.plank.compiler.buildIMul
import com.lorenzoog.plank.compiler.buildISub
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class BinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val lhs = !descriptor.lhs.toInstruction().codegen()
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Add -> buildIAdd(rhs, lhs, "add.tmp")
        Operation.Sub -> buildISub(rhs, lhs, "sub.tmp")
        Operation.Mul -> buildIMul(rhs, lhs, "mul.tmp")
        Operation.Div -> buildFDiv(rhs.toFloat(), lhs.toFloat(), "div.tmp")
        Operation.Concat -> {
          val concatFunction = runtime.concatFunction
            ?: return Left("concat function is null")

          buildCall(concatFunction, listOf(rhs, lhs))
        }
      }
    )
  }
}
