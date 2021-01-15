package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class ReferenceInstruction(private val descriptor: Expr.Reference) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return getReference(context, descriptor.expr)
  }

  companion object {
    fun getReference(context: PlankContext, descriptor: Expr): Value? {
      return when {
        descriptor is Expr.Access -> {
          val name = descriptor.name.text
            ?: return context.report("variable name is null", descriptor)

          context.findVariable(name)
            ?: return context.report("variable does not exists", descriptor)
        }
        Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor)) ||
          Builtin.Bool.isAssignableBy(context.binding.visit(descriptor)) -> {
          val type = context.map(context.binding.visit(descriptor))
            ?: return context.report("type is null", descriptor)

          val value = context.map(descriptor).codegen(context)
            ?: return context.report("value is null", descriptor)

          context.builder.createIntToPtr(value, type.getPointerType(), "reftmp")
        }
        else -> {
          val type = context.map(context.binding.visit(descriptor))
            ?: return context.report("type is null", descriptor)

          val value = context.map(descriptor).codegen(context)
            ?: return context.report("value is null", descriptor)

          context.builder.createLoad(type, value, "reftmp")
        }
      }
    }
  }
}
