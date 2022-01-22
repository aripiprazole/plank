package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.createScopeContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class ModuleInstruction(private val descriptor: ResolvedModuleDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    createScopeContext(descriptor.name.text) nestedScope@{
      addModule(this@nestedScope)

      descriptor.content.codegen()
    }

    runtime.nullConstant
  }
}
