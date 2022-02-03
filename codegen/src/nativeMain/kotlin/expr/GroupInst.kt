package org.plank.codegen.expr

import org.plank.analyzer.element.TypedGroupExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class GroupInst(private val descriptor: TypedGroupExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
