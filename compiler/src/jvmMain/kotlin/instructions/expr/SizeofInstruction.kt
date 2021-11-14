package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.getSize
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class SizeofInstruction(private val descriptor: Expr.Sizeof) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val struct = findStruct(descriptor.type.text)
      ?: return Left(unresolvedTypeError(descriptor.type.text))

    Right(struct.getSize())
  }
}
