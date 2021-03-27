package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildCall
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class CallInstruction(private val descriptor: Expr.Call) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> {
        val name = callee.name.text

        findFunction(name)
      }
      is Expr.Get -> {
        val name = callee.member.text

        findFunction(name)
      }
      else -> null
    } ?: return Left("callee is null")

    val function = callee.accessIn(this@codegen)
      ?: return Left("function is null")

    val arguments = descriptor.arguments
      .map { it.toInstruction() }
      .map { !it.codegen() }

    Right(buildCall(function, arguments))
  }
}
