package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedExprStmt
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class ExprInst(private val descriptor: ResolvedExprStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
