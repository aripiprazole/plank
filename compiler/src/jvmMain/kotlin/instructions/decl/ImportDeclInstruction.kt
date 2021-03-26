package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import org.llvm4j.llvm4j.Value

class ImportDeclInstruction(private val descriptor: Decl.ImportDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val module = context.findModule(descriptor.module.text)
      ?: return context.report("Could not find module", descriptor)

    context.expand(module)

    return null
  }
}
