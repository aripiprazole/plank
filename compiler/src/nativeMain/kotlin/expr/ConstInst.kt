package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedConstExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.codegenError
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
      is String -> createGlobalString(value, "string-constant") // add `Named` interface
      else -> codegenError("Unsupported constant type: ${value::class.simpleName}")
    }
  }
}
