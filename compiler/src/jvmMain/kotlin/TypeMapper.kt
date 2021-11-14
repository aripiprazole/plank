package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.Builtin
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.shared.Either
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import com.gabrielleeg1.plank.shared.map
import org.llvm4j.llvm4j.Type
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

typealias TypegenResult = Either<CodegenError, Type>

fun CompilerContext.toType(type: PlankType?): TypegenResult = either {
  Right(
    when (type) {
      Builtin.Void -> runtime.types.void
      Builtin.Int -> runtime.types.int
      Builtin.Double -> runtime.types.double
      Builtin.Numeric -> runtime.types.double
      Builtin.Bool -> runtime.types.i1
      Builtin.Char -> runtime.types.i8
      is PlankType.Delegate -> {
        return toType(type.delegate ?: return Left(unresolvedTypeError("delegate $type")))
      }
      is PlankType.Set -> {
        when (val result = findStruct(type.name)?.let(context::getPointerType)) {
          is Ok -> result.value
          is Err -> return Left(llvmError(result.error.message ?: "AssertionError"))
          null -> return Left(unresolvedTypeError(type.name))
        }
      }
      is PlankType.Struct -> {
        findStruct(type.name) ?: return Left(unresolvedTypeError(type.name))
      }
      is PlankType.Callable -> {
        val returnType = !type.returnType.toType()

        context.getFunctionType(
          returnType,
          *type.parameters.map { !it.toType() }.toTypedArray()
        )
      }
      is PlankType.Pointer -> {
        when (val result = !toType(type.inner).map(context::getPointerType)) {
          is Ok -> result.value
          is Err -> return Left(llvmError(result.error.message ?: "AssertionError"))
        }
      }
      is PlankType.Array -> {
        when (val result = toType(type.inner).bind().let(context::getPointerType)) {
          is Ok -> result.value
          is Err -> return Left(llvmError(result.error.message ?: "AssertionError"))
        }
      }
      else -> runtime.types.void
    }
  )
}
