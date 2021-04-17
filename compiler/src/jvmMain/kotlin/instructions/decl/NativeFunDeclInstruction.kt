package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.either

class NativeFunDeclInstruction(private val descriptor: Decl.FunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    addFunction(descriptor)
  }
}
