package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.compiler.PlankContext
import org.llvm4j.llvm4j.Value

abstract class PlankInstruction internal constructor() {
  abstract fun codegen(context: PlankContext): Value?
}
