package org.plank.codegen.expr

import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class SizeofInst(private val descriptor: TypedSizeofExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val type = descriptor.type.typegen()

    return type.size
  }
}
