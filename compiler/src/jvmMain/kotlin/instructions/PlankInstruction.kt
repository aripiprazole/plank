package com.lorenzoog.jplank.compiler.instructions

import com.lorenzoog.jplank.compiler.PlankContext
import org.llvm4j.llvm4j.Value

abstract class PlankInstruction internal constructor() {
  abstract fun codegen(context: PlankContext): Value?
}
