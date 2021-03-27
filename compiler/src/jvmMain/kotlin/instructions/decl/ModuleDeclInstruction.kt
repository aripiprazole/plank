package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ModuleDeclInstruction(private val descriptor: Decl.ModuleDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    createNestedScope(descriptor.name.text).also(::addModule).run {
      descriptor.content.forEach {
        !it.toInstruction().codegen()
      }
    }

    Right(runtime.nullConstant)
  }
}
