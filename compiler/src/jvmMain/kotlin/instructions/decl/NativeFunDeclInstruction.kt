package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.either

class NativeFunDeclInstruction(private val descriptor: Decl.FunDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    addFunction(descriptor)
  }
}
