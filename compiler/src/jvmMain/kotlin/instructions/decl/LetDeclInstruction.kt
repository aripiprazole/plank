package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class LetDeclInstruction(private val descriptor: Decl.LetDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text
    val pkType = context.binding.visit(descriptor.type) {
      context.binding.visit(descriptor.value)
    }

    val type = context.map(pkType) ?: return context.report("type is null", descriptor)

    val variable = context.builder.buildAlloca(type, Some(name)).also {
      context.addVariable(name, it)
    }

    val value = context.map(descriptor.value).codegen(context)
      ?: return context.report("variable value is null", descriptor)

    return context.builder.buildStore(value, variable)
  }
}
