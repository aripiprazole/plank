package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class StructInst(private val descriptor: ResolvedStructDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val name = descriptor.name.text
    val struct = createNamedStruct(name) {
      elements = descriptor.properties.values.map { it.type.typegen() }
    }

    addStruct(name, descriptor.ty, struct)

    return i1.constantNull
  }
}
