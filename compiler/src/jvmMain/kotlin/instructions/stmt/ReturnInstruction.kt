package com.lorenzoog.plank.compiler.instructions.stmt

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Value

class ReturnInstruction(private val descriptor: Stmt.ReturnStmt) : PlankInstruction() {

  override fun PlankContext.codegen(): Either<CodegenError, Value> = either {
    val value = descriptor.value
    val instruction = buildReturn(value?.toInstruction()?.codegen()?.bind())

    Right(instruction)
  }
}
