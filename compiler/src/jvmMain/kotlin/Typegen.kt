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
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import org.llvm4j.llvm4j.Type

fun CompilerContext.typegen(type: PlankType?): Type {
  return when (type) {
    UnitType -> runtime.types.void
    BoolType -> runtime.types.i1
    CharType -> runtime.types.i8
    is IntType -> if (type.floatingPoint) runtime.types.float else runtime.types.int // TODO
    is DelegateType -> typegen(type)
    is EnumType -> typegen(type)
    is StructType -> typegen(type)
    is FunctionType -> typegen(type)
    is PointerType -> typegen(type)
    is ArrayType -> typegen(type)
    else -> runtime.types.void
  }
}

private fun CompilerContext.typegen(type: DelegateType): Type {
  return type.value?.let(::typegen) ?: unresolvedTypeError("delegate $type")
}

private fun CompilerContext.typegen(type: EnumType): Type {
  return findStruct(type.name.text)?.let(::pointerType) ?: unresolvedTypeError(type.name.text)
}

private fun CompilerContext.typegen(type: StructType): Type =
  findStruct(type.name.text) ?: unresolvedTypeError(type.name.text)

private fun CompilerContext.typegen(type: FunctionType): Type {
  val name = "Closure_${type}_${type.hashCode()}_Function"
  module.getTypeByName(name).toNullable()?.let { return pointerType(it) }

  val parameter = type.parameter.typegen()
  val returnType = type.returnType.typegen()
  val environmentType = runtime.types.voidPtr

  val functionType = if (parameter.isVoidType()) {
    context.getFunctionType(returnType, environmentType)
  } else {
    context.getFunctionType(returnType, environmentType, parameter)
  }

  val struct = context.getNamedStructType(name).apply {
    setElementTypes(
      pointerType(functionType),
      runtime.types.voidPtr,
      isPacked = false
    )
  }

  return pointerType(struct)
}

private fun CompilerContext.typegen(type: PointerType): Type = pointerType(typegen(type.inner))

private fun CompilerContext.typegen(type: ArrayType): Type = pointerType(typegen(type.inner))
