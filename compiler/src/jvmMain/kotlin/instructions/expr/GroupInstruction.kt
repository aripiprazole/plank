package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedGroupExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class GroupInstruction(private val descriptor: TypedGroupExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return descriptor.expr.codegen()
  }
}
