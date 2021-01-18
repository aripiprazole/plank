package com.lorenzoog.jplank.compiler.converter

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PkType
import com.lorenzoog.jplank.compiler.PlankContext
import io.vexelabs.bitbuilder.llvm.ir.Value

interface DataTypeConverter {
  fun convertTo(context: PlankContext, value: Value, type: PkType): Value = when (type) {
    Builtin.Double -> convertToFloat(context, value)
    Builtin.Int -> convertToInt(context, value)
    else -> throw IllegalArgumentException("could not convert ${value.getType().getIR()} to $type")
  }

  fun convertToFloat(context: PlankContext, value: Value): Value

  fun convertToInt(context: PlankContext, value: Value): Value
}
