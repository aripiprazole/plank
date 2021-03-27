package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class AssignInstruction(private val descriptor: Expr.Assign) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val value = !descriptor.value.toInstruction().codegen()
    val variable = findVariable(descriptor.name.text)
      ?: return Left("Variable don't exist")

    Right(buildStore(variable, value))
  }
}
