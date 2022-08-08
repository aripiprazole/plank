package org.plank.codegen.expr

import org.plank.analyzer.element.TypedSetExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.findField
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Value

class SetInst(private val descriptor: TypedSetExpr) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val value = descriptor.value.codegen()
    val field = findField(descriptor.receiver, descriptor.info, descriptor.member)

    createStore(value, field)

    return field
  }
}
