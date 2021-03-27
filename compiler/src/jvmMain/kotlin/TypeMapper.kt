package com.lorenzoog.plank.compiler

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Type
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

typealias TypegenResult = Either<out CodegenError, out Type>

fun PlankContext.toType(type: PlankType?): TypegenResult = either {
  Right(
    when (type) {
      Builtin.Void -> runtime.types.void
      Builtin.Int -> runtime.types.int
      Builtin.Double -> runtime.types.double
      Builtin.Numeric -> runtime.types.double
      Builtin.Bool -> runtime.types.i1
      Builtin.Char -> runtime.types.i8
      is PlankType.Struct -> {
        findStruct(type.name)
          ?: return Left("can't find struct ${type.name}")
      }
      is PlankType.Callable -> {
        val returnType = !type.returnType.toType()

        context.getFunctionType(
          returnType,
          *type.parameters.map { !it.toType() }.toTypedArray()
        )
      }
      is PlankType.Pointer -> {
        when (val result = toType(type.inner).bind().let(context::getPointerType)) {
          is Ok -> result.value
          is Err -> return Left("can't convert type $type to llvm type")
        }
      }
      is PlankType.Array -> {
        when (val result = toType(type.inner).bind().let(context::getPointerType)) {
          is Ok -> result.value
          is Err -> return Left("can't convert type $type to llvm type")
        }
      }
      else -> runtime.types.void
    }
  )
}
