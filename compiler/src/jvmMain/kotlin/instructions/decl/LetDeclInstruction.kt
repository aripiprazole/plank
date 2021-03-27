package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class LetDeclInstruction(private val descriptor: Decl.LetDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text
    val type = !binding.visit(descriptor.type) { binding.visit(descriptor.value) }
      .toType()

    val variable = buildAlloca(type, name).also {
      addVariable(name, it)
    }

    val value = !descriptor.toInstruction().codegen()

    Right(buildStore(variable, value))
  }
}
