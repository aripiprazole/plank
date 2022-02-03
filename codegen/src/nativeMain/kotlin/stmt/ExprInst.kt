package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class ExprInst(private val descriptor: ResolvedExprStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
