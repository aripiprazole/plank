package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class ReferenceInstruction(private val descriptor: Expr.Reference) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return getReference(context, descriptor.expr)
  }

  companion object {
    fun getReference(context: PlankContext, descriptor: Expr): Value? {
      val type = context.binding.visit(descriptor)

      return when {
        descriptor is Expr.Access -> {
          val name = descriptor.name.text
            ?: return context.report("variable name is null", descriptor)

          context.findVariable(name)
            ?: return context.report("variable does not exists", descriptor)
        }
        Builtin.Numeric.isAssignableBy(type) || Builtin.Bool.isAssignableBy(type) -> {
          val mappedType = context.map(type)
            ?: return context.report("type is null", descriptor)

          val value = context.map(descriptor).codegen(context)
            ?: return context.report("value is null", descriptor)

          val reference = context.builder.buildAlloca(mappedType, name = Some("refallocatmp"))

          context.builder.buildStore(value, reference)

          reference
        }
        else -> {
          val value = context.map(descriptor).codegen(context)
            ?: return context.report("value is null", descriptor)

          context.builder.buildLoad(value, Some("reftmp"))
        }
      }
    }
  }
}
