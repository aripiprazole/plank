package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.expr.GetInstruction.Companion.findField
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class SetInstruction(private val descriptor: Expr.Set) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val value = !descriptor.value.toInstruction().codegen()
    val field = !findField(descriptor.receiver, descriptor.member)

    buildStore(value, field)

    Right(field)
  }
}
