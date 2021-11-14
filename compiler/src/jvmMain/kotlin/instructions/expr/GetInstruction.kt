package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import org.llvm4j.llvm4j.PointerType

class GetInstruction(private val descriptor: Expr.Get) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val field = !findField(descriptor.receiver, descriptor.property)

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
