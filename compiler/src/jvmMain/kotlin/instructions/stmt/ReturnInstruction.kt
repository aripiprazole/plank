package com.lorenzoog.jplank.compiler.instructions.stmt

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildRet
import com.lorenzoog.jplank.element.Stmt
import org.llvm4j.llvm4j.Value

class ReturnInstruction(private val descriptor: Stmt.ReturnStmt) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return if (context.binding.visit(descriptor).isAssignableBy(Builtin.Void)) {
      context.builder.buildRet()
    } else {
      val value = context
        .map(descriptor.value ?: return context.report("missing return value", descriptor))
        .codegen(context) ?: return context.report("value is null", descriptor)

      context.builder.buildRet(value)
    }
  }
}
