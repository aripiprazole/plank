package org.plank.codegen.expr

import org.plank.analyzer.element.TypedModuleSetExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.llvm4k.ir.Value

class ModuleSetInst(private val descriptor: TypedModuleSetExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: codegenError("Unable to find module `${descriptor.module.name.text}`")

    val variable = module.getSymbol(descriptor.member.text)

    val value = descriptor.value.codegen()

    return createStore(value, variable)
  }
}
