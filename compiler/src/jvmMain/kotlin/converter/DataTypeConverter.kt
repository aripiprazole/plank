package com.lorenzoog.plank.compiler.converter

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.PlankContext
import org.llvm4j.llvm4j.Value

interface DataTypeConverter {
  fun convertTo(context: PlankContext, value: Value, type: PlankType): Value = when (type) {
    Builtin.Double -> convertToFloat(context, value)
    Builtin.Int -> convertToInt(context, value)
    else -> {
      throw IllegalArgumentException("could not convert ${value.getType().getAsString()} to $type")
    }
  }

  fun convertToFloat(context: PlankContext, value: Value): Value

  fun convertToInt(context: PlankContext, value: Value): Value
}
