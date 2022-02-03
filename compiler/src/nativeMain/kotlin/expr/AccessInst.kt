package org.plank.compiler.expr

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class AccessInst(private val descriptor: TypedAccessExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when {
      descriptor.type.isNested -> findSymbol(descriptor.name.text)
      else -> createLoad(findSymbol(descriptor.name.text))
    }
  }
}
