package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import arrow.core.traverseEither
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
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import org.llvm4j.llvm4j.Type
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

typealias TypegenResult = Either<CodegenViolation, Type>

fun CompilerContext.convertType(type: PlankType?): TypegenResult = either.eager {
  when (type) {
    UnitType -> runtime.types.void
    BoolType -> runtime.types.i1
    CharType -> runtime.types.i8
    is IntType -> if (type.floatingPoint) runtime.types.float else runtime.types.int // TODO
    is DelegateType -> convertType(type).bind()
    is EnumType -> convertType(type).bind()
    is StructType -> convertType(type).bind()
    is FunctionType -> convertType(type).bind()
    is PointerType -> convertType(type).bind()
    is ArrayType -> convertType(type).bind()
    else -> runtime.types.void
  }
}

private fun CompilerContext.convertType(type: DelegateType): Either<CodegenViolation, Type> =
  either.eager {
    val value = type.value ?: unresolvedTypeError("delegate $type").left().bind<PlankType>()

    convertType(value).bind()
  }

private fun CompilerContext.convertType(type: EnumType): Either<CodegenViolation, Type> =
  when (val result = findStruct(type.name.text)?.let(context::getPointerType)) {
    is Ok -> result.value.right()
    is Err -> llvmError(result.error.message ?: "AssertionError").left()
    null -> unresolvedTypeError(type.name.text).left()
  }

private fun CompilerContext.convertType(type: StructType): Either<CodegenViolation, Type> =
  findStruct(type.name.text)?.right() ?: unresolvedTypeError(type.name.text).left()

private fun CompilerContext.convertType(type: FunctionType): Either<CodegenViolation, Type> =
  either.eager {
    val parameters = type.parameters
      .map { type ->
        type.cast<FunctionType>()?.copy(isClosure = true)?.convertType()?.bind()
          ?: type.convertType().bind()
      }
      .toTypedArray()

    when (type.isClosure) {
      true -> {
        val name = "Closure_${type.hashCode()}_Function"
        module.getTypeByName(name).toNullable()?.let { return@eager it }

        val returnType = type.returnType.convertType().bind()
        val environmentType = runtime.types.voidPtr

        val functionType = context.getFunctionType(returnType, environmentType, *parameters)

        val struct = context.getNamedStructType(name).apply {
          setElementTypes(
            context.getPointerType(functionType).unwrap(),
            runtime.types.voidPtr,
            isPacked = false
          )
        }

        context.getPointerType(struct).unwrap()
      }
      false -> {
        val returnType = type.returnType.convertType().bind()
        val functionType = context.getFunctionType(
          returnType,
          parameters = type.parameters.traverseEither { it.convertType() }.bind().toTypedArray()
        )

        when (val result = context.getPointerType(functionType)) {
          is Ok -> result.value
          is Err -> llvmError(result.error.message ?: "AssertionError").left().bind<Type>()
        }
      }
    }
  }

private fun CompilerContext.convertType(type: PointerType): Either<CodegenViolation, Type> =
  either.eager {
    when (val result = convertType(type.inner).map(context::getPointerType).bind()) {
      is Ok -> result.value
      is Err -> llvmError(result.error.message ?: "AssertionError").left().bind<Type>()
    }
  }

private fun CompilerContext.convertType(type: ArrayType): Either<CodegenViolation, Type> =
  either.eager {
    when (val result = convertType(type.inner).bind().let(context::getPointerType)) {
      is Ok -> result.value
      is Err -> llvmError(result.error.message ?: "AssertionError").left().bind<Type>()
    }
  }
