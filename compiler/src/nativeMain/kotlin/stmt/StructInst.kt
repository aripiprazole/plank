package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class StructInst(private val descriptor: ResolvedStructDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val name = descriptor.name.text
    val struct = createNamedStruct(name) {
      elements = descriptor.properties.values.map { it.type.typegen() }
    }

    addStruct(name, descriptor.type, struct)

    return i1.constantNull
  }
}
