package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedGetExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.findField
import org.plank.llvm4k.ir.Value

class GetInst(private val descriptor: TypedGetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createLoad(findField(descriptor.receiver, descriptor.member))
  }
}
