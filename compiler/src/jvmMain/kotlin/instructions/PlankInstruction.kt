package com.lorenzoog.jplank.compiler.instructions

import com.lorenzoog.jplank.compiler.PlankContext
import io.vexelabs.bitbuilder.llvm.ir.Value

abstract class PlankInstruction internal constructor() {
  abstract fun codegen(context: PlankContext): Value?
}
