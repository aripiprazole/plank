package com.lorenzoog.plank.compiler.converter

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildFPToUI
import com.lorenzoog.plank.compiler.buildUIToFP
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.expectedTypeError
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

val FLOAT_TYPES = listOf(
  TypeKind.Float,
  TypeKind.Double,
  TypeKind.BFloat,
  TypeKind.FP128
)

val INT_TYPES = listOf(
  TypeKind.Integer
)

class DataTypeConverter {
  fun convertToFloat(context: CompilerContext, value: Value): CodegenResult = either {
    val type = value.getType().getTypeKind()
    if (type in FLOAT_TYPES) {
      return Right(value)
    }

    Right(
      when (type) {
        TypeKind.Integer -> {
          context.buildUIToFP(value, context.runtime.types.double, "conv.tmp")
        }
        else -> return Left(context.expectedTypeError(Builtin.Int::class))
      }
    )
  }

  fun convertToInt(context: CompilerContext, value: Value): CodegenResult = either {
    val type = value.getType().getTypeKind()
    if (type in INT_TYPES) {
      return Right(value)
    }

    Right(
      when (type) {
        TypeKind.Float,
        TypeKind.Double,
        TypeKind.FP128,
        TypeKind.BFloat -> {
          context.buildFPToUI(value, context.runtime.types.int, "conv.tmp")
        }
        else -> return Left(context.expectedTypeError(Builtin.Double::class))
      }
    )
  }
}
