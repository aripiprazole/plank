package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class ValueInstruction(private val descriptor: Expr.Value) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val reference = !descriptor.expr.toInstruction().codegen()

    Right(buildLoad(reference, "value.tmp"))
  }
}
