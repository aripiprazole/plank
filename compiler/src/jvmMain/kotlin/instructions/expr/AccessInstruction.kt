package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class AccessInstruction(private val descriptor: Expr.Access) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val value = findVariable(descriptor.name.text)
      ?: return Left(unresolvedVariableError(descriptor.name.text))

    Right(buildLoad(value))
  }
}
