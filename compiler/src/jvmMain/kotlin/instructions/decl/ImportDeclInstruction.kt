package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ImportDeclInstruction(private val descriptor: Decl.ImportDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val module = findModule(descriptor.module.text)
      ?: return Left("Could not find module")

    expand(module)

    Right(runtime.nullConstant)
  }
}
