package com.lorenzoog.plank.compiler.instructions.stmt

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Value

class ExprStmtInstruction(private val descriptor: Stmt.ExprStmt) : PlankInstruction() {
  override fun PlankContext.codegen(): Either<CodegenError, Value> = either {
    descriptor.expr.toInstruction().codegen()
  }
}
