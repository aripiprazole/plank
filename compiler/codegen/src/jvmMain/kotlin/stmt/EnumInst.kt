package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.type.EnumType
import org.plank.codegen.type.RankedType
import org.plank.llvm4k.ir.Value

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val enum = EnumType(descriptor)
    addType(descriptor.name.text, RankedType(enum, descriptor.info.scheme))

    return i1.constantNull
  }
}
