package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.analyzer.element.TypedRefExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class RefInstruction(private val descriptor: TypedRefExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return findReference(descriptor.expr)
  }

  companion object {
    fun CompilerContext.findReference(descriptor: TypedExpr): Value {
      return when (descriptor) {
        is TypedInstanceExpr -> InstanceInstruction(descriptor, isPointer = true).codegen()
        is TypedAccessExpr -> findVariable(descriptor.name.text)
        else -> {
          val type = descriptor.type.typegen()
          val value = descriptor.codegen()

          val reference = buildAlloca(type, "ref.alloca.tmp")

          buildStore(value, value)

          reference
        }
      }
    }
  }
}
