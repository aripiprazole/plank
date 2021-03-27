package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Value

class ConstInstruction(private val descriptor: Expr.Const) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return when (val value = descriptor.value) {
      is Double -> context.runtime.types.double.getConstant(value.toDouble())
      is Int -> context.runtime.types.int.getConstant(value.toInt())
      is Byte -> context.runtime.types.i8.getConstant(value.toInt())
      is Short -> context.runtime.types.i16.getConstant(value.toInt())
      is Float -> context.runtime.types.float.getConstant(value.toDouble())
      is Boolean -> if (value) context.runtime.trueConstant else context.runtime.falseConstant
      is String -> Value(LLVM.LLVMBuildGlobalStringPtr(context.builder.ref, value, "str"))
      else -> context.report("unsupported type ${value::class}")
    }
  }
}
