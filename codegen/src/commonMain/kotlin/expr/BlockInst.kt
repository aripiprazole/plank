package org.plank.codegen.expr

import org.plank.analyzer.element.TypedBlockExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.codegen.element.addClosure
import org.plank.llvm4k.ir.Value

class BlockInst(private val descriptor: TypedBlockExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val symbol = addClosure(
      name = "anonymous$${descriptor.hashCode()}",
      returnTy = descriptor.ty,
      references = descriptor.references,
    ) {
      descriptor.stmts.codegen()

      createRet(descriptor.value.codegen())
    }

    val closure = symbol.access()
      ?: codegenError("Failed to access generated closure for block expr")

    return callClosure(closure)
  }
}
