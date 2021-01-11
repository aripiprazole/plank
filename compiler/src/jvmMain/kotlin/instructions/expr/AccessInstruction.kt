package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class AccessInstruction(private val descriptor: Expr.Access) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text ?: return context.report("variable name is null", descriptor)

    return context.findVariable(name)
      ?: return context.report("variable does not exists", descriptor)
  }
}
