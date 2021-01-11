package com.lorenzoog.jplank.compiler.instructions.stmt

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.getType
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Stmt
import io.vexelabs.bitbuilder.llvm.ir.Value

class ReturnInstruction(private val descriptor: Stmt.ReturnStmt) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return if (descriptor.getType(context.binding).isAssignableBy(Builtin.Void)) {
      context.builder.createRetVoid()
    } else {
      val value = context
        .map(descriptor.value ?: return context.report("missing return value", descriptor))
        .codegen(context) ?: return context.report("value is null", descriptor)

      context.builder.createRet(value)
    }
  }
}
