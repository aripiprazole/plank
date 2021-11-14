package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFieldError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.either

class InstanceInstruction(
  private val descriptor: Expr.Instance,
  private val isPointer: Boolean = false,
) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = descriptor.struct.text

    // todo fix me finding only global types
    val type = binding.findStruct(Expr.Access(descriptor.struct, descriptor.location))
      ?: return Left(unresolvedTypeError(name))

    val struct = findStruct(name) ?: return Left(unresolvedTypeError(name))

    val arguments = type.fields
      .map { field ->
        val (_, value) = descriptor.arguments.entries.find { it.key.text == field.name }
          ?: return Left(unresolvedFieldError(field.name, type))

        !value.toInstruction().codegen()
      }
      .toTypedArray()

    getInstance(struct, *arguments, isPointer = isPointer)
  }
}
