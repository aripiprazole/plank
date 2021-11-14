package com.gabrielleeg1.plank.compiler

import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Constant
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.Type

fun Type.getSize(): Constant {
  return Constant(LLVM.LLVMSizeOf(ref))
}

fun Function.verify(): Boolean {
  return LLVM.LLVMVerifyFunction(ref, LLVM.LLVMPrintMessageAction) == 0
}
