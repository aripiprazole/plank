package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedStructDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class StructDeclInstruction(private val descriptor: ResolvedStructDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val name = descriptor.name.text
    val struct = context.getNamedStructType(name).also { struct ->
      struct.setElementTypes(
        *descriptor.properties
          .map { (_, property) -> property.type.toType().bind() }
          .toTypedArray(),
        isPacked = false
      )
    }

    addStruct(name, descriptor.type, struct)

    runtime.nullConstant
  }
}
