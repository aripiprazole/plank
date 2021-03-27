package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Value

class ModuleDeclInstruction(private val descriptor: Decl.ModuleDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val moduleContext = context.createNestedScope(descriptor.name.text)
      .also(context::addModule)

    descriptor.content.forEach {
      moduleContext.map(it).codegen(moduleContext)
    }

    return null
  }
}
