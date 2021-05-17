package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ClassDeclInstruction(private val descriptor: Decl.StructDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text
    val struct = context.getNamedStructType(name).also { struct ->
      struct.setElementTypes(
        *descriptor.fields
          .map { !binding.visit(it.type).toType() }
          .toTypedArray(),
        isPacked = false
      )
    }

    addStruct(name, struct)

    Right(runtime.nullConstant)
  }
}
