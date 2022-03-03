package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.type.RankedType
import org.plank.codegen.type.StructType
import org.plank.llvm4k.ir.Value

class StructInst(private val descriptor: ResolvedStructDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    addType(descriptor.name.text, RankedType(StructType(descriptor), descriptor.info.scheme))

    return i1.constantNull
  }
}
