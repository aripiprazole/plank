package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.TypedDerefExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class DerefInstruction(private val descriptor: TypedDerefExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    buildLoad(descriptor.expr.codegen().bind(), "value.tmp")
  }
}
