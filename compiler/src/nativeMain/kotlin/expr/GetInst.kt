package org.plank.compiler.expr

import org.plank.analyzer.element.TypedGetExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.findField
import org.plank.llvm4k.ir.Value

class GetInst(private val descriptor: TypedGetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createLoad(findField(descriptor.receiver, descriptor.member))
  }
}
