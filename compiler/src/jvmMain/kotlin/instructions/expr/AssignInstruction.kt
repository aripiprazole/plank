package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.TypedAssignExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import org.llvm4j.llvm4j.AllocaInstruction

class AssignInstruction(private val descriptor: TypedAssignExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val value = descriptor.value.toInstruction().codegen().bind()
    val variable = findVariable(descriptor.name.text)
      ?: unresolvedVariableError(descriptor.name.text)
        .left()
        .bind<AllocaInstruction>()

    buildStore(variable, value)
  }
}
