package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GetInstruction.Companion.findField
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class SetInstruction(private val descriptor: Expr.Set) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val value = !descriptor.value.toInstruction().codegen()
    val field = !findField(descriptor.receiver, descriptor.property)

    buildStore(field, value)

    Right(field)
  }
}
