package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.Either
import com.gabrielleeg1.plank.analyzer.element.ResolvedDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Function

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String
  abstract val descriptor: ResolvedDecl

  /** Access the function in the [context] */
  abstract fun accessIn(context: CompilerContext): Function?

  /** Generates the function in the [this] */
  abstract override fun CompilerContext.codegen(): Either<CodegenError, Function>
}
