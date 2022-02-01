package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedSetExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.findField
import org.plank.llvm4k.ir.Value

class SetInst(private val descriptor: TypedSetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val field = findField(descriptor.receiver, descriptor.member)

    createStore(value, field)

    return field
  }
}
