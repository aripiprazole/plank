package com.gabrielleeg1.plank.compiler.instructions.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedExprStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class ExprStmtInstruction(private val descriptor: ResolvedExprStmt) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult =
    descriptor.expr.codegen()
}
