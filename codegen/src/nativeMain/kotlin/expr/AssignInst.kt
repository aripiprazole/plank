package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAssignExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class AssignInst(private val descriptor: TypedAssignExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val variable = findSymbol(descriptor.name.text)

    return createStore(value, variable)
  }
}
