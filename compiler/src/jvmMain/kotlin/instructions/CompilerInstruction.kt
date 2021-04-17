package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.shared.Either
import org.llvm4j.llvm4j.Value

typealias CodegenResult = Either<CodegenError, Value>

abstract class CompilerInstruction internal constructor() {
  abstract fun CompilerContext.codegen(): CodegenResult
}
