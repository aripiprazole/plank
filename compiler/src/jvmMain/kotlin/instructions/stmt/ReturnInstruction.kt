package com.gabrielleeg1.plank.compiler.instructions.stmt

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class ReturnInstruction(private val descriptor: ResolvedReturnStmt) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    buildReturn(descriptor.value?.toInstruction()?.codegen()?.bind())
  }
}
