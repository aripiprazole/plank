package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.createUnit
import org.plank.llvm4k.ir.Value

class ReturnInst(private val descriptor: ResolvedReturnStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createRet(descriptor.value?.codegen() ?: createUnit())
  }
}
