package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.visit
import io.vexelabs.bitbuilder.llvm.ir.Value

class LetDeclInstruction(private val descriptor: Decl.LetDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text ?: return context.report("variable name is null", descriptor)
    val pkType = context.binding.visit(descriptor.type) {
      context.binding.visit(descriptor.value)
    }

    val type = context.map(pkType) ?: return context.report("type is null", descriptor)

    val variable = context.builder.createAlloca(type, name).also {
      context.addVariable(name, it)
    }

    val value = context.map(descriptor.value).codegen(context)
      ?: return context.report("variable value is null", descriptor)

    return context.builder.createStore(value, variable)
  }
}
