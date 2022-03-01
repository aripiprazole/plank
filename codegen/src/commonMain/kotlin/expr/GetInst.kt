package org.plank.codegen.expr

import org.plank.analyzer.element.TypedGetExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.findField
import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.Value

class GetInst(private val descriptor: TypedGetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createLoad(findField(descriptor.receiver, descriptor.info, descriptor.member))
  }
}
