package com.gabrielleeg1.plank.compiler.instructions

import arrow.core.Either
import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.Value

typealias CodegenResult = Either<CodegenViolation, Value>

interface CompilerInstruction {
  fun CompilerContext.codegen(): CodegenResult
}
