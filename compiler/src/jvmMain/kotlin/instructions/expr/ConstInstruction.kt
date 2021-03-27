package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Value

class ConstInstruction(private val descriptor: Expr.Const) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult {
    return Right(
      when (val value = descriptor.value) {
        is Double -> runtime.types.double.getConstant(value.toDouble())
        is Int -> runtime.types.int.getConstant(value.toInt())
        is Byte -> runtime.types.i8.getConstant(value.toInt())
        is Short -> runtime.types.i16.getConstant(value.toInt())
        is Float -> runtime.types.float.getConstant(value.toDouble())
        is Boolean -> if (value) runtime.trueConstant else runtime.falseConstant
        is String -> Value(LLVM.LLVMBuildGlobalStringPtr(builder.ref, value, "str"))
        else -> return Left("unsupported constant type ${value::class.simpleName}")
      }
    )
  }
}
