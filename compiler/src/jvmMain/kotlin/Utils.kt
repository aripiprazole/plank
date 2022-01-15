package com.gabrielleeg1.plank.compiler

import arrow.core.computations.EitherEffect
import arrow.core.left
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Constant
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.Type

suspend fun <A> EitherEffect<A, *>.returnError(a: A): A {
  return a.left().bind<A>()
}

fun Type.getSize(): Constant {
  return Constant(LLVM.LLVMSizeOf(ref))
}

fun Function.verify(): Boolean {
  return LLVM.LLVMVerifyFunction(ref, LLVM.LLVMPrintMessageAction) == 0
}
