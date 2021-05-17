package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildGlobalStringPtr
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.invalidConstantError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right

class ConstInstruction(private val descriptor: Expr.Const) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return Right(
      when (val value = descriptor.value) {
        is Double -> runtime.types.double.getConstant(value.toDouble())
        is Int -> runtime.types.int.getConstant(value.toInt())
        is Byte -> runtime.types.i8.getConstant(value.toInt())
        is Short -> runtime.types.i16.getConstant(value.toInt())
        is Float -> runtime.types.float.getConstant(value.toDouble())
        is Boolean -> if (value) runtime.trueConstant else runtime.falseConstant
        is String -> buildGlobalStringPtr(value, "str")
        else -> return Left(invalidConstantError(value))
      }
    )
  }
}
