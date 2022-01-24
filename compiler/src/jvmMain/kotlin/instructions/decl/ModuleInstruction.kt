package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.createScopeContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class ModuleInstruction(private val descriptor: ResolvedModuleDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    createScopeContext(descriptor.name.text) nestedScope@{
      addModule(this@nestedScope)

      descriptor.content.codegen()
    }

    return runtime.nullConstant
  }
}
