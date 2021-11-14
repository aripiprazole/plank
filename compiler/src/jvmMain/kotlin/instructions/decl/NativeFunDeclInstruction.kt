package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.either

class NativeFunDeclInstruction(private val descriptor: Decl.FunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    addFunction(descriptor)
  }
}
