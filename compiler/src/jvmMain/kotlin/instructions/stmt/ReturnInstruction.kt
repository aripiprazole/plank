package com.lorenzoog.plank.compiler.instructions.stmt

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Value

class ReturnInstruction(private val descriptor: Stmt.ReturnStmt) : CompilerInstruction() {

  override fun CompilerContext.codegen(): Either<CodegenError, Value> = either {
    Right(buildReturn(descriptor.value?.toInstruction()?.codegen()?.bind()))
  }
}
