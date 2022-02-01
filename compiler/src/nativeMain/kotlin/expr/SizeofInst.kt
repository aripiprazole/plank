package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedSizeofExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class SizeofInst(private val descriptor: TypedSizeofExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val type = descriptor.type.typegen()

    return type.size
  }
}
