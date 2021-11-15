package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import org.llvm4j.llvm4j.AllocaInstruction

class AccessInstruction(private val descriptor: TypedAccessExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val value = findVariable(descriptor.name.text)
      ?: unresolvedVariableError(descriptor.name.text)
        .left()
        .bind<AllocaInstruction>()

    buildLoad(value)
  }
}
