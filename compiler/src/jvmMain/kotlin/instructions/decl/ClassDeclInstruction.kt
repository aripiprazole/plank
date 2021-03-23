package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import org.llvm4j.llvm4j.Value

class ClassDeclInstruction(private val descriptor: Decl.ClassDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text ?: return context.report("name is null", descriptor)

    context.addStructure(
      name,
      context.llvm.getNamedStructType(name).also { struct ->
        struct.setElementTypes(
          *descriptor.fields.map { (_, _, type) ->
            context.map(context.binding.visit(type))
              ?: return context.report("failed to handle argument", type)
          }.toTypedArray(),
          isPacked = false
        )
      }
    )

    return null
  }
}
