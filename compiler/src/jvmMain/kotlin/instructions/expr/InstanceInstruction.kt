package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFieldError
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

class InstanceInstruction(
  private val descriptor: TypedInstanceExpr,
  private val isPointer: Boolean = false,
) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val struct = descriptor.type.typegen()

    if (struct !is NamedStructType) {
      llvmError("TODO")
    }

    val arguments = descriptor.type.properties
      .map { (name, property) ->
        val (_, value) = descriptor.arguments.entries.find { it.key == property.name }
          ?: unresolvedFieldError(name.text, property.type)

        value.codegen()
      }
      .toTypedArray()

    return getInstance(struct, *arguments, isPointer = isPointer)
  }
}
