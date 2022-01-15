package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.ResolvedImportDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedModuleError

class ImportInstruction(private val descriptor: ResolvedImportDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val module = findModule(descriptor.module.name.text)
      ?: unresolvedModuleError(descriptor.module.name.text).left().bind<CompilerContext>()

    expand(module)

    runtime.nullConstant
  }
}
