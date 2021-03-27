package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Function

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String
  abstract val descriptor: Decl

  /** Access the function in the [context] */
  abstract fun accessIn(context: PlankContext): Function?

  /** Generates the function in the [this] */
  abstract override fun PlankContext.codegen(): CodegenResult
}
