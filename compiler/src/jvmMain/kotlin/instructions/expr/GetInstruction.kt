package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.StructType
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedGetExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Value

class GetInstruction(private val descriptor: TypedGetExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return buildLoad(findField(descriptor.receiver, descriptor.member), "load.tmp")
  }

  companion object {
    fun CompilerContext.findField(receiver: TypedExpr, name: Identifier): Value {
      val struct = when (receiver) {
        is TypedAccessExpr -> receiver.name.text
        else -> receiver.type.name.text
      }

      val instance = when (receiver) {
        is TypedAccessExpr -> findVariable(receiver.name.text)
        else -> receiver.codegen()
      }

      val alloca = when (instance) {
        is AllocaInstruction -> instance
        else -> {
          val alloca = buildAlloca(instance.getType(), "alloca.tmp")
          buildStore(alloca, instance)
          alloca
        }
      }

      if (!receiver.type.isInstance<StructType>()) {
        unresolvedTypeError(receiver.type.name.text)
      }

      val propertyIndex = receiver.type
        .cast<StructType>()!!.properties.entries
        .indexOfFirst { it.key == name }

      return getField(alloca, propertyIndex, "$struct.${name.text}")
    }
  }
}
