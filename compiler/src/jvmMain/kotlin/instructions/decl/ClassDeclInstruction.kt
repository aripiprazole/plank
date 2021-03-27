package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Value

class ClassDeclInstruction(private val descriptor: Decl.StructDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text

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
