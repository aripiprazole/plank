package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.Value

class StructInst(private val descriptor: ResolvedStructDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val name = descriptor.name.text
    val struct = createNamedStruct(name) {
      elements = descriptor.members.values.map { it.ty.typegen() }
    }

    addStruct(name, struct)

    return i1.constantNull
  }
}
