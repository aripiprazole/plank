package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.shared.Either
import org.llvm4j.llvm4j.Value

typealias CodegenError = String
typealias CodegenResult = Either<out CodegenError, out Value>

abstract class PlankInstruction internal constructor() {
  abstract fun PlankContext.codegen(): CodegenResult
}
