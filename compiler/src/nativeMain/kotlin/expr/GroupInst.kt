package org.plank.compiler.expr

import org.plank.analyzer.element.TypedGroupExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class GroupInst(private val descriptor: TypedGroupExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
