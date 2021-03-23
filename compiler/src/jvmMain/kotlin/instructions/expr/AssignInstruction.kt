package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildStore
import com.lorenzoog.jplank.element.Expr
import org.llvm4j.llvm4j.Value

class AssignInstruction(private val descriptor: Expr.Assign) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text
      ?: return context.report("variable name is null", descriptor)

    val value = context.map(descriptor.value).codegen(context)
      ?: return context.report("variable value is null", descriptor)

    val variable = context.findVariable(name)
      ?: return context.report("variable is null", descriptor)

    return context.builder.buildStore(variable, value)
  }
}
