package org.plank.codegen.expr

import org.plank.analyzer.checker.fullPath
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.Value

class AccessInst(private val descriptor: TypedAccessExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.scope.fullPath().text) ?: this

    return createLoad(module.getSymbol(this, descriptor.name.text, descriptor.subst))
  }
}
