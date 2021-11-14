package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedModuleError
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class ImportDeclInstruction(private val descriptor: Decl.ImportDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val module = findModule(descriptor.module.text)
      ?: return Left(unresolvedModuleError(descriptor.module.text))

    expand(module)

    Right(runtime.nullConstant)
  }
}
