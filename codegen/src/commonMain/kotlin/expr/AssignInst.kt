package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.resolver.fullPath
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class AssignInst(private val descriptor: TypedAssignExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.scope.fullPath().text) ?: this

    val value = descriptor.value.codegen()
    val variable = module.getSymbol(this, descriptor.name.text)

    return createStore(value, variable)
  }
}
