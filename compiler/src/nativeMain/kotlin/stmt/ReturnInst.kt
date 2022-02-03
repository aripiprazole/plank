package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.createUnit
import org.plank.llvm4k.ir.Value

class ReturnInst(private val descriptor: ResolvedReturnStmt) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createRet(descriptor.value?.codegen() ?: createUnit())
  }
}
