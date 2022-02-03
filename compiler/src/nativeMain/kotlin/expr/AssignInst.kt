package org.plank.compiler.expr

import org.plank.analyzer.element.TypedAssignExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class AssignInst(private val descriptor: TypedAssignExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val variable = findSymbol(descriptor.name.text)

    return createStore(value, variable)
  }
}
