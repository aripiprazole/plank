package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.DelegateType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.map
import org.llvm4j.llvm4j.Type
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

typealias TypegenResult = Either<CodegenError, Type>

fun CompilerContext.toType(type: PlankType?) = either.eager {
  when (type) {
    PlankType.void -> runtime.types.void
    PlankType.int -> runtime.types.int
    PlankType. -> runtime.types.double
    PlankType.bool -> runtime.types.i1
    PlankType.char -> runtime.types.i8
    is DelegateType -> {
      toType(type.value ?: return Left(unresolvedTypeError("delegate $type")))
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
}
