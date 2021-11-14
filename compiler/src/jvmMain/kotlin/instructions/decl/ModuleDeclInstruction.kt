package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class ModuleDeclInstruction(private val descriptor: Decl.ModuleDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    createNestedScope(descriptor.name.text) nestedScope@{
      this@codegen.addModule(this@nestedScope)

      descriptor.content.forEach {
        !it.toInstruction().codegen()
      }
    }

    Right(runtime.nullConstant)
  }
}
