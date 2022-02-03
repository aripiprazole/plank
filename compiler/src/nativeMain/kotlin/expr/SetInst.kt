package org.plank.compiler.expr

import org.plank.analyzer.element.TypedSetExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.findField
import org.plank.llvm4k.ir.Value

class SetInst(private val descriptor: TypedSetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val field = findField(descriptor.receiver, descriptor.member)

    createStore(value, field)

    return field
  }
}
