package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr

class GroupInstruction(private val descriptor: Expr.Group) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return descriptor.expr.toInstruction().codegen()
  }
}
