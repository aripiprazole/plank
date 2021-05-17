package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class AssignInstruction(private val descriptor: Expr.Assign) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val value = !descriptor.value.toInstruction().codegen()
    val variable = findVariable(descriptor.name.text)
      ?: return Left(unresolvedVariableError(descriptor.name.text))

    Right(buildStore(variable, value))
  }
}
