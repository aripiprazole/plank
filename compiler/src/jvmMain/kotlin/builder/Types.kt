package com.gabrielleeg1.plank.compiler.builder

import arrow.core.Either
import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.ArrayType
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.VectorType

fun CompilerContext.unsafePointerType(type: Type): PointerType {
  return context.getPointerType(type).unwrap()
}

fun CompilerContext.pointerType(type: Type): Either<AssertionError, PointerType> {
  return context.getPointerType(type).arrow()
}

fun CompilerContext.unsafeArrayType(type: Type, size: Int): ArrayType {
  return context.getArrayType(type, size).unwrap()
}

fun CompilerContext.arrayType(type: Type, size: Int): Either<AssertionError, ArrayType> {
  return context.getArrayType(type, size).arrow()
}

fun CompilerContext.unsafeVectorType(type: Type, size: Int): VectorType {
  return context.getVectorType(type, size).unwrap()
}

fun CompilerContext.vectorType(type: Type, size: Int): Either<AssertionError, VectorType> {
  return context.getVectorType(type, size).arrow()
}
