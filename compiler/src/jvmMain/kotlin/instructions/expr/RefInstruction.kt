package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.analyzer.element.TypedRefExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction

class RefInstruction(private val descriptor: TypedRefExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult {
    return findReference(descriptor.expr)
  }

  companion object {
    fun CompilerContext.findReference(descriptor: TypedExpr): CodegenResult = either.eager {
      when (descriptor) {
        is TypedInstanceExpr -> InstanceInstruction(descriptor, isPointer = true).codegen().bind()
        is TypedAccessExpr -> findVariable(descriptor.name.text).bind()
        else -> {
          val type = descriptor.type.typegen().bind()
          val value = descriptor.codegen().bind()

          val reference = buildAlloca(type, "ref.alloca.tmp")

          buildStore(value, value)

          reference
        }
      }
    }
  }
}
