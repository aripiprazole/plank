package com.lorenzoog.plank.compiler.instructions.stmt

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Stmt
import org.llvm4j.llvm4j.Value

class ExprStmtInstruction(private val descriptor: Stmt.ExprStmt) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return context.map(descriptor.expr).codegen(context)
  }
}
