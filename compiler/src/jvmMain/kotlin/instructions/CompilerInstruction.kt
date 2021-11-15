package com.gabrielleeg1.plank.compiler.instructions

import arrow.core.Either
import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.Value

typealias CodegenResult = Either<CodegenError, Value>

abstract class CompilerInstruction internal constructor() {
  abstract fun CompilerContext.codegen(): CodegenResult
}
