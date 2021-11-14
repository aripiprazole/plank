package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class LetDeclInstruction(private val descriptor: Decl.LetDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text
    val type = binding.findBound(descriptor)!!

    val variable = buildAlloca(!type.toType(), name).also {
      addVariable(name, type, it)
    }

    val value = !descriptor.value.toInstruction().codegen()

    Right(buildStore(variable, value))
  }
}
