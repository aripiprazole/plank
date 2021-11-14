package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class StructDeclInstruction(private val descriptor: Decl.StructDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text
    val type = binding.visit(descriptor)
    val struct = context.getNamedStructType(name).also { struct ->
      struct.setElementTypes(
        *descriptor.properties
          .map { !binding.visit(it.type).toType() }
          .toTypedArray(),
        isPacked = false
      )
    }

    addStruct(name, type, struct)

    Right(runtime.nullConstant)
  }
}
