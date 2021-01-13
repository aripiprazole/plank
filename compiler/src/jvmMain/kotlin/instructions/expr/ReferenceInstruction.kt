package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class ReferenceInstruction(private val descriptor: Expr.Reference) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return when (val expr = descriptor.expr) {
      is Expr.Access -> {
        val name = expr.name.text
          ?: return context.report("variable name is null", descriptor)

        context.findVariable(name) ?: return context.report("variable does not exists", descriptor)
      }
      else -> {
        val type = context.map(context.binding.visit(descriptor))
          ?: return context.report("type is null", descriptor)

        val value = context.map(descriptor.expr).codegen(context)
          ?: return context.report("value is null", descriptor)

        context.builder.createLoad(type, value, "reftmp")
      }
    }
  }
}
