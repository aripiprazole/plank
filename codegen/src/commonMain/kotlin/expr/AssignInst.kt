package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAssignExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.llvm4k.ir.Value

class AssignInst(private val descriptor: TypedAssignExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = descriptor.module?.name?.text
      ?.let { findModule(it) ?: codegenError("Unable to find module `$it`") }
      ?: this

    val value = descriptor.value.codegen()
    val variable = module.getSymbol(descriptor.name.text)

    return createStore(value, variable)
  }
}
