package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAccessModuleExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.llvm4k.ir.Value

class AccessModuleInst(private val descriptor: TypedAccessModuleExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: codegenError("Unable to find module `${descriptor.module.name.text}`")

    return module.getSymbol(descriptor.member.text)
  }
}
