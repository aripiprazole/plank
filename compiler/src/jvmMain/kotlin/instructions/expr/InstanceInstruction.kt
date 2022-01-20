package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFieldError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.NamedStructType

class InstanceInstruction(
  private val descriptor: TypedInstanceExpr,
  private val isPointer: Boolean = false,
) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val struct = descriptor.type.convertType().bind()

    ensure(struct is NamedStructType) { llvmError("TODO") }

    val arguments = descriptor.type.properties
      .map { (name, property) ->
        val (_, value) = descriptor.arguments.entries.find { it.key == property.name }
          ?: unresolvedFieldError(name.text, property.type)
            .left()
            .bind<Map.Entry<Identifier, TypedExpr>>()

        value.codegen().bind()
      }
      .toTypedArray()

    getInstance(struct as NamedStructType, *arguments, isPointer = isPointer).bind()
  }
}
