package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.getSize
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class SizeofInstruction(private val descriptor: Expr.Sizeof) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val struct = findStruct(descriptor.name.text)
      ?: return Left("struct is null")

    Right(struct.getSize())
  }
}
