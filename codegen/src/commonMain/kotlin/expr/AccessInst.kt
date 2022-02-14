package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.llvm4k.ir.Value

class AccessInst(private val descriptor: TypedAccessExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = descriptor.module?.name?.text
      ?.let { findModule(it) ?: codegenError("Unable to find module `$it`") }
      ?: this

    return when {
      descriptor.ty.isNested -> module.getSymbol(descriptor.name.text)
      else -> createLoad(module.getSymbol(descriptor.name.text))
    }
  }
}
