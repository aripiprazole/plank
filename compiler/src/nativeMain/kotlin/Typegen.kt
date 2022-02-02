package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.ArrayType
import com.gabrielleeg1.plank.analyzer.BoolType
import com.gabrielleeg1.plank.analyzer.CharType
import com.gabrielleeg1.plank.analyzer.DelegateType
import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.IntType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.PointerType
import com.gabrielleeg1.plank.analyzer.StructType
import com.gabrielleeg1.plank.analyzer.UnitType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.FunctionType as LLVMFunctionType

@Suppress("Detekt.ComplexMethod")
fun CodegenContext.typegen(type: PlankType): Type {
  return when (type) {
    UnitType -> getOrCreateStruct("unit") { elements = listOf(i8) }
    BoolType -> i1
    CharType -> i8
    is IntType -> if (type.floatingPoint) float else i32 // TODO
    is DelegateType -> type.value?.typegen() ?: codegenError("Delegate type has no value")
    is EnumType -> {
      findStruct(type.name.text) ?: codegenError("Unresolved enum `${type.name.text}`")
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

      val struct = getOrCreateStruct("closure.anonymous.($type)") {
        elements = listOf(functionType.pointer(), i8.pointer())
      }

      return struct.pointer()
    }
    is ArrayType -> type.inner.typegen().pointer()
    is PointerType -> type.inner.typegen().pointer()
    else -> codegenError("Unsupported type: $type")
  }
}
