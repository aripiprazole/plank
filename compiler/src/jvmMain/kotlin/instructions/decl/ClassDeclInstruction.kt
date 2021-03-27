package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ClassDeclInstruction(private val descriptor: Decl.StructDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text

    addStruct(
      name,
      context.getNamedStructType(name).also { struct ->
        struct.setElementTypes(
          *descriptor.fields
            .map { !binding.visit(it.type).toType() }
            .toTypedArray(),
          isPacked = false
        )
      }
    )

    Right(runtime.nullConstant)
  }
}
