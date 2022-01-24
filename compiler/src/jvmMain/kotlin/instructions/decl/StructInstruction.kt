package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedStructDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class StructInstruction(private val descriptor: ResolvedStructDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val name = descriptor.name.text
    val struct = context.getNamedStructType(name).also { struct ->
      struct.setElementTypes(
        *descriptor.properties
          .map { (_, property) -> property.type.typegen() }
          .toTypedArray(),
        isPacked = false
      )
    }

    addStruct(name, descriptor.type, struct)

    return runtime.nullConstant
  }
}
