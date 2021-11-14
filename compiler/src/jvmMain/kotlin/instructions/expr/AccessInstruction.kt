package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class AccessInstruction(private val descriptor: Expr.Access) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val value = findVariable(descriptor.name.text)
      ?: return Left(unresolvedVariableError(descriptor.name.text))

    Right(buildLoad(value))
  }
}
