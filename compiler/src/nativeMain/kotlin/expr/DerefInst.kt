package org.plank.compiler.expr

import org.plank.analyzer.element.TypedDerefExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class DerefInst(private val descriptor: TypedDerefExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createLoad(descriptor.expr.codegen())
  }
}
