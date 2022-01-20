package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addIrClosure
import com.gabrielleeg1.plank.compiler.instructions.element.generateBody

class ClosureFunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    addIrClosure(descriptor, generateBody(descriptor)).bind()
  }
}
