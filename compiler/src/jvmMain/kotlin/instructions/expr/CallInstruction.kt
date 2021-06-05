package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildCall
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedFunctionError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class CallInstruction(private val descriptor: Expr.Call) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> findFunction(callee.name.text)
      is Expr.Get -> findFunction(callee.property.text)
      else -> null
    }

    val function = callee
      ?.accessIn(this@codegen)
      ?: return Left(unresolvedFunctionError(descriptor.callee))

    val arguments = descriptor.arguments
      .map { it.toInstruction() }
      .map { !it.codegen() }

    Right(buildCall(function, arguments))
  }
}
