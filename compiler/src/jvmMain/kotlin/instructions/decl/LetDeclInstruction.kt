package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

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
