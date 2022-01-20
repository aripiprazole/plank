package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class AccessInstruction(private val descriptor: TypedAccessExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    buildLoad(findVariable(descriptor.name.text).bind())
  }
}
