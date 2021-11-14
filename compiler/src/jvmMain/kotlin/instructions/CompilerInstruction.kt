package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.shared.Either
import org.llvm4j.llvm4j.Value

typealias CodegenResult = Either<CodegenError, Value>

abstract class CompilerInstruction internal constructor() {
  abstract fun CompilerContext.codegen(): CodegenResult
}
