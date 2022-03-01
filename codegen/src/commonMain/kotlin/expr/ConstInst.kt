package org.plank.codegen.expr

import org.plank.analyzer.element.TypedConstExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.codegen.createUnit
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.stringMangled
import org.plank.llvm4k.ir.Value

class ConstInst(private val descriptor: TypedConstExpr) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    return when (val value = descriptor.value) {
      is Int -> i32.getConstant(value.toInt(), false)
      is Byte -> i8.getConstant(value.toInt(), false)
      is Short -> i16.getConstant(value.toInt(), false)
      is Float -> float.getConstant(value)
      is Double -> double.getConstant(value)
      is Boolean -> if (value) i1.getConstant(1, false) else i1.getConstant(0, false)
      is String -> createGlobalStringPtr(value, stringMangled { "string.const" }.get())
      is Unit -> createUnit()
      else -> codegenError("Unsupported constant type: ${value::class.simpleName}")
    }
  }
}
