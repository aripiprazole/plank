package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.getType
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.utils.FunctionUtils
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

class CallInstruction(private val descriptor: Expr.Call) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val type = descriptor.getType(context.binding)

    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> {
        val name = callee.name.text ?: return context.report("name is null", descriptor)
        val scope = context.binding.getScope(callee)
          ?: return context.report("scope is null", descriptor)

        context.module.getFunction(
          FunctionUtils.generateName(name, scope.name)
        )
      }

      else -> context.map(callee).codegen(context)
    } ?: return context.report("callee is null", descriptor)

    if (callee !is FunctionValue) {
      return context.report("callee is not a function", descriptor)
    }

    val arguments = descriptor.arguments
      .map {
        context.map(it).codegen(context)
          ?: return context.report("failed to handle argument", it)
      }

    val variable = if (type.isVoid) "" else "calltmp"

    return context.builder.createCall(callee, arguments, variable)
  }
}
