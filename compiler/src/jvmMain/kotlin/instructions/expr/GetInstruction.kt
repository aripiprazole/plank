package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.builder.getField
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.PointerType

class GetInstruction(private val descriptor: Expr.Get) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val field = !findField(descriptor.receiver, descriptor.member)

    Right(buildLoad(field, "load.tmp"))
  }

  companion object {
    fun CompilerContext.findField(receiver: Expr, name: Identifier): CodegenResult = either {
      val instance = when (receiver) {
        is Expr.Access -> {
          findVariable(receiver.name.text)
            ?: return Left(unresolvedVariableError(receiver.name.text))
        }
        else -> !receiver.toInstruction().codegen()
      }
      val struct = PointerType(instance.getType().ref).getSubtypes().first().getAsString()

      val type = findType { (_, type) ->
        type.getAsString() == struct
      } ?: return Left(unresolvedTypeError("unknown"))

      getField(instance, type.fields.indexOfFirst { it.name == name.text })
    }
  }
}
