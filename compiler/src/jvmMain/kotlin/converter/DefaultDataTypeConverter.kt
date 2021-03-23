package com.lorenzoog.jplank.compiler.converter

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.llvm.buildFPToUI
import com.lorenzoog.jplank.compiler.llvm.buildUIToFP
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class DefaultDataTypeConverter : DataTypeConverter {
  override fun convertToFloat(context: PlankContext, value: Value): Value {
    val type = value.getType().getTypeKind()
    if (type in FLOAT_TYPES) {
      return value
    }

    return when (type) {
      TypeKind.Integer -> {
        context.builder.buildUIToFP(value, context.runtime.types.double, "conv.tmp")
      }
      else -> throw IllegalArgumentException("could not convert $type to float")
    }
  }

  override fun convertToInt(context: PlankContext, value: Value): Value {
    val type = value.getType().getTypeKind()
    if (type in INT_TYPES) {
      return value
    }

    return when (type) {
      TypeKind.Float,
      TypeKind.Double,
      TypeKind.FP128,
      TypeKind.BFloat -> context.builder.buildFPToUI(value, context.runtime.types.int, "conv.tmp")
      else -> throw IllegalArgumentException("could not convert $type to float")
    }
  }

  companion object {
    val FLOAT_TYPES = listOf(
      TypeKind.Float,
      TypeKind.Double,
      TypeKind.BFloat,
      TypeKind.FP128
    )

    val INT_TYPES = listOf(
      TypeKind.Integer
    )
  }
}
