package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.StructType
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedGetExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.AllocaInstruction

class GetInstruction(private val descriptor: TypedGetExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    buildLoad(findField(descriptor.receiver, descriptor.member).bind(), "load.tmp")
  }

  companion object {
    fun CompilerContext.findField(receiver: TypedExpr, name: Identifier): CodegenResult =
      either.eager {
        val instance = when (receiver) {
          is TypedAccessExpr -> findVariable(receiver.name.text).bind()
          else -> receiver.codegen().bind()
        }

        val alloca = when (instance) {
          is AllocaInstruction -> instance
          else -> {
            val alloca = buildAlloca(instance.getType(), "alloca.tmp")
            buildStore(alloca, instance)
            alloca
          }
        }

        ensure(receiver.type.isInstance<StructType>()) {
          unresolvedTypeError(receiver.type.name.text)
        }

        val propertyIndex = receiver.type
          .cast<StructType>()!!.properties.entries
          .indexOfFirst { it.key == name }

        getField(alloca, propertyIndex).bind()
      }
  }
}
