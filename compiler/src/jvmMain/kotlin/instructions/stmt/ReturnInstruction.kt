package com.gabrielleeg1.plank.compiler.instructions.stmt

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.shared.Either
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import org.llvm4j.llvm4j.Value

class ReturnInstruction(private val descriptor: Stmt.ReturnStmt) : CompilerInstruction() {

  override fun CompilerContext.codegen(): Either<CodegenError, Value> = either {
    Right(buildReturn(descriptor.value?.toInstruction()?.codegen()?.bind()))
  }
}
