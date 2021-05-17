package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Expr

class GroupInstruction(private val descriptor: Expr.Group) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return descriptor.expr.toInstruction().codegen()
  }
}
