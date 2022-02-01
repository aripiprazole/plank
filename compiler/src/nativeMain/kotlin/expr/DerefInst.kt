package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedDerefExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class DerefInst(private val descriptor: TypedDerefExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createLoad(descriptor.expr.codegen())
  }
}
