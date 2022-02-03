package org.plank.codegen.expr

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class AccessInst(private val descriptor: TypedAccessExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when {
      descriptor.type.isNested -> findSymbol(descriptor.name.text)
      else -> createLoad(findSymbol(descriptor.name.text))
    }
  }
}
