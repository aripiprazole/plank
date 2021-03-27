package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.Value

class SetInstruction(private val descriptor: Expr.Set) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val value = context.map(descriptor.value).codegen(context)
      ?: return context.report("value is null", descriptor)

    val variable = GetInstruction.getVariable(
      context,
      descriptor,
      descriptor.receiver,
      descriptor.member,
    ) ?: return context.report("variable is null", descriptor)

    context.builder.buildStore(value, variable)

    return variable
  }
}
