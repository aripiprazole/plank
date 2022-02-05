package org.plank.codegen

import org.plank.analyzer.ArrayType
import org.plank.analyzer.BoolType
import org.plank.analyzer.CharType
import org.plank.analyzer.DelegateType
import org.plank.analyzer.EnumType
import org.plank.analyzer.FunctionType
import org.plank.analyzer.IntType
import org.plank.analyzer.PlankType
import org.plank.analyzer.PointerType
import org.plank.analyzer.StructType
import org.plank.analyzer.UnitType
import org.plank.analyzer.Untyped
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.FunctionType as LLVMFunctionType

@Suppress("Detekt.ComplexMethod")
fun CodegenContext.typegen(type: PlankType): Type {
  return when (type) {
    UnitType -> unit
    BoolType -> i1
    CharType -> i8
    is IntType -> if (type.floatingPoint) float else i32 // TODO
    is DelegateType -> type.value?.typegen() ?: codegenError("Delegate type has no value")
    is EnumType -> {
      findStruct(type.name.text)?.pointer() ?: codegenError("Unresolved enum `${type.name.text}`")
    }
    is StructType -> {
      findStruct(type.name.text) ?: codegenError("Unresolved struct `${type.name.text}`")
    }
    is FunctionType -> {
      val parameter = type.parameter.typegen()
      val returnType = type.returnType.typegen()

      val functionType = if (parameter.kind == Type.Kind.Void) {
        LLVMFunctionType(returnType, i8.pointer())
      } else {
        LLVMFunctionType(returnType, i8.pointer(), parameter)
      }

      getOrCreateStruct("closure.anonymous.($type)") {
        elements = listOf(functionType.pointer(), i8.pointer())
      }
    }
    is ArrayType -> type.inner.typegen().pointer()
    is PointerType -> type.inner.typegen().pointer()
    is Untyped -> void
    else -> codegenError("Unsupported type: $type")
  }
}
