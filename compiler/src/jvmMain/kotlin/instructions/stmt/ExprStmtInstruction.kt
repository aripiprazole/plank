package com.lorenzoog.jplank.compiler.instructions.stmt

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Stmt
import io.vexelabs.bitbuilder.llvm.ir.Value

class ExprStmtInstruction(private val descriptor: Stmt.ExprStmt) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return context.map(descriptor.expr).codegen(context)
  }
}
