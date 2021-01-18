package com.lorenzoog.jplank.compiler.converter

import com.lorenzoog.jplank.compiler.PlankContext
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.Value

class DefaultDataTypeConverter : DataTypeConverter {
  override fun convertToFloat(context: PlankContext, value: Value): Value {
    val type = value.getType().getTypeKind()
    if (type in FLOAT_TYPES) {
      return value
    }

    return when (type) {
      TypeKind.Integer -> {
        context.builder.createUIToFP(value, context.runtime.types.double, "conv.tmp")
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
      TypeKind.X86_FP80,
      TypeKind.FP128,
      TypeKind.PPC_FP128,
      TypeKind.BFloat -> context.builder.createFPToUI(value, context.runtime.types.int, "conv.tmp")
      else -> throw IllegalArgumentException("could not convert $type to float")
    }
  }

  companion object {
    val FLOAT_TYPES = listOf(
      TypeKind.Float,
      TypeKind.Double,
      TypeKind.X86_FP80,
      TypeKind.PPC_FP128,
      TypeKind.BFloat,
      TypeKind.FP128
    )

    val INT_TYPES = listOf(
      TypeKind.Integer
    )
  }
}
