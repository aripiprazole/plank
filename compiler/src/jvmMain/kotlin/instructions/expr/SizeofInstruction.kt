package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.getSize
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class SizeofInstruction(private val descriptor: Expr.Sizeof) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val struct = findStruct(descriptor.type.text)
      ?: return Left(unresolvedTypeError(descriptor.type.text))

    Right(struct.getSize())
  }
}
