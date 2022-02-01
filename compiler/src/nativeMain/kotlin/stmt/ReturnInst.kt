package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.createUnit
import org.plank.llvm4k.ir.Value

class ReturnInst(private val descriptor: ResolvedReturnStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createRet(descriptor.value?.codegen() ?: createUnit())
  }
}
