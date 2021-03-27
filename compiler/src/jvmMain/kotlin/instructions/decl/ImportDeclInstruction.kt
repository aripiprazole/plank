package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Value

class ImportDeclInstruction(private val descriptor: Decl.ImportDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val module = context.findModule(descriptor.module.text)
      ?: return context.report("Could not find module", descriptor)

    context.expand(module)

    return null
  }
}
