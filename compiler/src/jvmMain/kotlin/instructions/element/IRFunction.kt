package com.lorenzoog.jplank.compiler.instructions.element

import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String
  abstract val descriptor: Decl

  fun call(context: PlankContext, arguments: List<Expr>): Value? {
    val type = context.binding.visit(descriptor) as? PlankType.Callable
      ?: return context.report("callable type is null", descriptor)

    val valueArguments = arguments
      .mapIndexed { i, expr ->
        val realExpr = if (type.parameters[i].isAny) {
          context.runtime.createObject(context, expr)
        } else {
          context.map(expr).codegen(context)
        }

        realExpr ?: return context.report("failed to handle argument", expr)
      }

    val function = access(context) ?: return context.report("function is null", descriptor)

    return context.builder.createCall(function, valueArguments)
  }

  /** Access the function in the [context] */
  abstract fun access(context: PlankContext): FunctionValue?

  /** Generates the function in the [context] */
  abstract override fun codegen(context: PlankContext): FunctionValue?
}
