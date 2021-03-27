package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.compiler.instructions.expr.GetInstruction.Companion.findField
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class SetInstruction(private val descriptor: Expr.Set) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val value = !descriptor.value.toInstruction().codegen()
    val field = !findField(descriptor.receiver, descriptor.member)

    buildStore(value, field)

    Right(field)
  }
}
