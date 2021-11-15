package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.StructType
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedGetExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Value

class GetInstruction(private val descriptor: TypedGetExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    buildLoad(findField(descriptor.receiver, descriptor.member).bind(), "load.tmp")
  }

  companion object {
    fun CompilerContext.findField(receiver: TypedExpr, name: Identifier): CodegenResult =
      either.eager {
        val instance = when (receiver) {
          is TypedAccessExpr -> {
            findVariable(receiver.name.text)
              ?: unresolvedVariableError(receiver.name.text)
                .left()
                .bind<Value>()
          }
          else -> receiver.toInstruction().codegen().bind()
        }
        val struct = PointerType(instance.getType().ref).getSubtypes().first().getAsString()

        val type = findType { (_, type) -> type.getAsString() == struct }
          ?: unresolvedTypeError("unknown")
            .left()
            .bind<PlankType>()

        ensure(type is StructType) { unresolvedTypeError("unknown") }

        getField(
          instance,
          (type as StructType).properties.entries.indexOfFirst { it.key == name }
        ).bind()
      }
  }
}
