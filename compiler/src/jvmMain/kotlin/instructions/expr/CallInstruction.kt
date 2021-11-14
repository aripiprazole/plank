package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildCall
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

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
