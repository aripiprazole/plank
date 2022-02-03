package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class ExprInst(private val descriptor: ResolvedExprStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
