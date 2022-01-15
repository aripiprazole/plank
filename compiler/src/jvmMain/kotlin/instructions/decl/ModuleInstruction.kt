package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class ModuleInstruction(private val descriptor: ResolvedModuleDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    createNestedScope(descriptor.name.text) nestedScope@{
      this@codegen.addModule(this@nestedScope)

      descriptor.content.forEach {
        it.toInstruction().codegen().bind()
      }
    }

    runtime.nullConstant
  }
}
