package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.None

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String
  abstract val descriptor: Decl

  fun call(context: PlankContext, arguments: List<Expr>): Value? {
    val valueArguments = arguments
      .map { expr ->
        context.map(expr).codegen(context)
          ?: return context.report("failed to handle argument", expr)
      }

    val function = access(context) ?: return context.report("function is null", descriptor)

    return context.builder.buildCall(function, *valueArguments.toTypedArray(), name = None)
  }

  /** Access the function in the [context] */
  abstract fun access(context: PlankContext): Function?

  /** Generates the function in the [context] */
  abstract override fun codegen(context: PlankContext): Function?
}
