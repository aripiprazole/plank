package com.gabrielleeg1.plank.compiler

import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Constant
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.Value

inline fun <reified A : Value> Value.unsafeCast(): A {
  return A::class.constructors.first().call(ref)
}

inline fun <reified A : Type> Type.unsafeCast(): A {
  return A::class.constructors.first().call(ref)
}

fun Type.getSize(): Constant {
  return Constant(LLVM.LLVMSizeOf(ref))
}

fun PointerType.getContainedType(): Type {
  return Type(LLVM.LLVMGetElementType(ref))
}

fun Function.verify(): Boolean {
  return LLVM.LLVMVerifyFunction(ref, LLVM.LLVMPrintMessageAction) == 0
}
