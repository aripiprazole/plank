package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedImportDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedModuleError
import org.llvm4j.llvm4j.Value

class ImportInstruction(private val descriptor: ResolvedImportDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: unresolvedModuleError(descriptor.module.name.text)

    expand(module)

    return runtime.nullConstant
  }
}
