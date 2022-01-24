package com.gabrielleeg1.plank.compiler.instructions.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedExprStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class ExprStmtInstruction(private val descriptor: ResolvedExprStmt) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
