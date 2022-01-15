package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.expectedTypeError
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

val FLOAT_TYPES = listOf(
  TypeKind.Float,
  TypeKind.Double,
  TypeKind.BFloat,
  TypeKind.FP128
)

val INT_TYPES = listOf(TypeKind.Integer)

fun CompilerContext.convertToFloat(value: Value): Either<CodegenError, Value> {
  val type = value.getType().getTypeKind()
  if (type in FLOAT_TYPES) {
    return value.right()
  }

  return when (type) {
    TypeKind.Integer -> buildUIToFP(value, runtime.types.double, "conv.tmp").right()
    else -> expectedTypeError(PlankType::class).left()
  }
}

fun CompilerContext.convertToInt(value: Value): Either<CodegenError, Value> {
  val type = value.getType().getTypeKind()
  if (type in INT_TYPES) {
    return value.right()
  }

  return when (type) {
    TypeKind.Float,
    TypeKind.Double,
    TypeKind.FP128,
    TypeKind.BFloat -> buildFPToUI(value, runtime.types.int, "conv.tmp").right()
    else -> expectedTypeError(PlankType::class).left()
  }
}
