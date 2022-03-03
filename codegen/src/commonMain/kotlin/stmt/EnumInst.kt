package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.type.EnumType
import org.plank.llvm4k.ir.Value

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    addType(descriptor.name.text, EnumType(descriptor))

    return i1.constantNull
  }
}
