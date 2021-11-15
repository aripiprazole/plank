package com.gabrielleeg1.plank.compiler.converter

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildFPToUI
import com.gabrielleeg1.plank.compiler.buildUIToFP
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.expectedTypeError
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

val FLOAT_TYPES = listOf(
  TypeKind.Float,
  TypeKind.Double,
  TypeKind.BFloat,
  TypeKind.FP128
)

val INT_TYPES = listOf(
  TypeKind.Integer
)

class DataTypeConverter {
  fun convertToFloat(context: CompilerContext, value: Value): CodegenResult = either.eager {
    val type = value.getType().getTypeKind()
    if (type in FLOAT_TYPES) {
      return@eager value
    }

    when (type) {
      TypeKind.Integer -> {
        context.buildUIToFP(value, context.runtime.types.double, "conv.tmp")
      }
      else -> context.expectedTypeError(PlankType::class)
        .left().bind<Value>()
    }
  }

  fun convertToInt(context: CompilerContext, value: Value): CodegenResult = either.eager {
    val type = value.getType().getTypeKind()
    if (type in INT_TYPES) {
      return@eager value
    }

    when (type) {
      TypeKind.Float,
      TypeKind.Double,
      TypeKind.FP128,
      TypeKind.BFloat -> {
        context.buildFPToUI(value, context.runtime.types.int, "conv.tmp")
      }
      else -> context.expectedTypeError(PlankType::class)
        .left()
        .bind<Value>()
    }
  }
}
