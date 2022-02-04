package org.plank.codegen.expr

import org.plank.analyzer.element.TypedConstExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.codegen.mangle
import org.plank.llvm4k.ir.Value

class ConstInst(private val descriptor: TypedConstExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when (val value = descriptor.value) {
      is Int -> i32.getConstant(value.toInt())
      is Byte -> i8.getConstant(value.toInt())
      is Short -> i16.getConstant(value.toInt())
      is Float -> float.getConstant(value)
      is Double -> double.getConstant(value)
      is Boolean -> if (value) i1.getConstant(1) else i1.getConstant(0)
      is String -> createGlobalStringPtr(value, mangle("string.const")) // add `Named` interface
      else -> codegenError("Unsupported constant type: ${value::class.simpleName}")
    }
  }
}
