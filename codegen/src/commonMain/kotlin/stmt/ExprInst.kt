package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Value

class ExprInst(private val descriptor: ResolvedExprStmt) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
