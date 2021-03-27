package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class AccessInstruction(private val descriptor: Expr.Access) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val value = findVariable(descriptor.name.text)
      ?: return Left("Variable don't exist")

    Right(buildLoad(value))
  }
}
