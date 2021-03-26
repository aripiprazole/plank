package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import org.llvm4j.llvm4j.Value

class ModuleDeclInstruction(private val descriptor: Decl.ModuleDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val moduleContext = context.createNestedScope(descriptor.name.text!!)
      .also(context::addModule)

    descriptor.content.forEach {
      moduleContext.map(it).codegen(moduleContext)
    }

    return null
  }
}
