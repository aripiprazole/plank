package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.builder.getInstance
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedFieldError
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.either

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
