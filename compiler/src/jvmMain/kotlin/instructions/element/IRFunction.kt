package com.lorenzoog.jplank.compiler.instructions.element

import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.llvm.buildCall
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.Value

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String
  abstract val descriptor: Decl

  fun call(context: PlankContext, arguments: List<Expr>): Value? {
    val type = context.binding.visit(descriptor) as? PlankType.Callable
      ?: return context.report("callable type is null", descriptor)

    val valueArguments = arguments
      .map { expr ->
        context.map(expr).codegen(context)
          ?: return context.report("failed to handle argument", expr)
      }

    val function = access(context) ?: return context.report("function is null", descriptor)

    return context.builder.buildCall(function, context.map(type)!!, valueArguments)
  }

  /** Access the function in the [context] */
  abstract fun access(context: PlankContext): Function?

  /** Generates the function in the [context] */
  abstract override fun codegen(context: PlankContext): Function?
}
