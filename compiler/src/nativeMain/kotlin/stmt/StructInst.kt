package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedStructDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
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
