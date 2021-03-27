package com.lorenzoog.plank.compiler.converter

import com.lorenzoog.plank.compiler.PlankContext
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class DefaultDataTypeConverter : DataTypeConverter {
  override fun convertToFloat(context: PlankContext, value: Value): Value {
    val type = value.getType().getTypeKind()
    if (type in FLOAT_TYPES) {
      return value
    }

    return when (type) {
      TypeKind.Integer -> {
        context.builder
          .buildUnsignedToFloat(value, context.runtime.types.double, Some("conv.tmp"))
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
      TypeKind.BFloat -> {
        context.builder
          .buildFloatToUnsigned(value, context.runtime.types.int, Some("conv.tmp"))
      }
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
