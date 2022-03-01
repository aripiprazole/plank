package org.plank.codegen.expr

import org.plank.analyzer.element.TypedDerefExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Value

class DerefInst(private val descriptor: TypedDerefExpr) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    return createLoad(descriptor.value.codegen())
  }
}
