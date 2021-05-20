package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildGEP
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class GetInstruction(private val descriptor: Expr.Get) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val field = !findField(descriptor.receiver, descriptor.member)

    Right(buildLoad(field, "load.tmp"))
  }

  companion object {
    fun CompilerContext.findField(receiver: Expr, name: Identifier): CodegenResult = either {
      val member = name.text
      val struct = receiver.toInstruction().codegen().bind()
      val type = findType { it.second == struct.getType() }
        ?: return Left(unresolvedTypeError("unknown"))

      val index = type.fields.indexOfFirst { it.name == member }
      val indices = listOf(
        runtime.types.int.getConstant(0),
        runtime.types.int.getConstant(index),
      )

      Right(buildGEP(struct, indices, name = "gep.tmp"))
    }
  }
}
