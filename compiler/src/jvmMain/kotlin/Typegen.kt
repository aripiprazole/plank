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
import com.gabrielleeg1.plank.compiler.builder.unsafePointerType
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import org.llvm4j.llvm4j.Type
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

typealias TypegenResult = Either<CodegenViolation, Type>

fun CompilerContext.typegen(type: PlankType?): TypegenResult = either.eager {
  when (type) {
    UnitType -> runtime.types.void
    BoolType -> runtime.types.i1
    CharType -> runtime.types.i8
    is IntType -> if (type.floatingPoint) runtime.types.float else runtime.types.int // TODO
    is DelegateType -> typegen(type).bind()
    is EnumType -> typegen(type).bind()
    is StructType -> typegen(type).bind()
    is FunctionType -> typegen(type).bind()
    is PointerType -> typegen(type).bind()
    is ArrayType -> typegen(type).bind()
    else -> runtime.types.void
  }
}

private fun CompilerContext.typegen(type: DelegateType): Either<CodegenViolation, Type> =
  either.eager {
    val value = type.value ?: unresolvedTypeError("delegate $type").left().bind<PlankType>()

    typegen(value).bind()
  }

private fun CompilerContext.typegen(type: EnumType): Either<CodegenViolation, Type> =
  when (val result = findStruct(type.name.text)?.let(context::getPointerType)) {
    is Ok -> result.value.right()
    is Err -> llvmError(result.error.message ?: "AssertionError").left()
    null -> unresolvedTypeError(type.name.text).left()
  }

private fun CompilerContext.typegen(type: StructType): Either<CodegenViolation, Type> =
  findStruct(type.name.text)?.right() ?: unresolvedTypeError(type.name.text).left()

private fun CompilerContext.typegen(type: FunctionType): Either<CodegenViolation, Type> =
  either.eager {
    val parameters = type.parameters
      .map { type ->
        type.cast<FunctionType>()?.copy(isClosure = true)?.typegen()?.bind()
          ?: type.typegen().bind()
      }
      .toTypedArray()

    when (type.isClosure) {
      true -> {
        val name = "Closure_${type.hashCode()}_Function"
        module.getTypeByName(name).toNullable()?.let { return@eager it }

        val returnType = type.returnType.typegen().bind()
        val environmentType = runtime.types.voidPtr

        val functionType = context.getFunctionType(returnType, environmentType, *parameters)

        val struct = context.getNamedStructType(name).apply {
          setElementTypes(
            unsafePointerType(functionType),
            runtime.types.voidPtr,
            isPacked = false
          )
        }

        unsafePointerType(struct)
      }
      false -> {
        val returnType = type.returnType.typegen().bind()
        val functionType = context.getFunctionType(
          returnType,
          parameters = type.parameters.traverseEither { it.typegen() }.bind().toTypedArray()
        )

        unsafePointerType(functionType)
      }
    }
  }

private fun CompilerContext.typegen(type: PointerType): Either<CodegenViolation, Type> =
  either.eager {
    when (val result = typegen(type.inner).map(context::getPointerType).bind()) {
      is Ok -> result.value
      is Err -> llvmError(result.error.message ?: "AssertionError").left().bind<Type>()
    }
  }

private fun CompilerContext.typegen(type: ArrayType): Either<CodegenViolation, Type> =
  either.eager {
    when (val result = typegen(type.inner).bind().let(context::getPointerType)) {
      is Ok -> result.value
      is Err -> llvmError(result.error.message ?: "AssertionError").left().bind<Type>()
    }
  }
