package org.plank.codegen.expr

import org.plank.analyzer.element.TypedGroupExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.Value

class GroupInst(private val descriptor: TypedGroupExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return descriptor.value.codegen()
  }
}
