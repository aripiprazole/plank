package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.ArrayType
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.VectorType

fun CompilerContext.getOrCreateStruct(name: String, builder: NamedStructType.() -> Unit = {}): NamedStructType {
  return module.getTypeByName(name).toNullable()
    ?: context.getNamedStructType(name).apply(builder)
}

fun CompilerContext.pointerType(type: Type): PointerType {
  return context.getPointerType(type).unwrap()
}

fun CompilerContext.arrayType(type: Type, size: Int): ArrayType {
  return context.getArrayType(type, size).unwrap()
}

fun CompilerContext.vectorType(type: Type, size: Int): VectorType {
  return context.getVectorType(type, size).unwrap()
}
