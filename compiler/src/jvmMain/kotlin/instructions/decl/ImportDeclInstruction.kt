package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedModuleError
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ImportDeclInstruction(private val descriptor: Decl.ImportDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val module = findModule(descriptor.module.text)
      ?: return Left(unresolvedModuleError(descriptor.module.text))

    expand(module)

    Right(runtime.nullConstant)
  }
}
