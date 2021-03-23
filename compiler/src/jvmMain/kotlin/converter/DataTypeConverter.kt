package com.lorenzoog.jplank.compiler.converter

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.PlankContext
import org.llvm4j.llvm4j.Value

interface DataTypeConverter {
  fun convertTo(context: PlankContext, value: Value, type: PlankType): Value = when (type) {
    Builtin.Double -> convertToFloat(context, value)
    Builtin.Int -> convertToInt(context, value)
    else -> throw IllegalArgumentException("could not convert ${value.getType().getAsString()} to $type")
  }

  fun convertToFloat(context: PlankContext, value: Value): Value

  fun convertToInt(context: PlankContext, value: Value): Value
}
