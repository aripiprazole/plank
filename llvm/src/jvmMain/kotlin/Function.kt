package com.lorenzoog.jplank.compiler.llvm

import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Function

fun Function.verify(action: Int): Boolean {
  return LLVM.LLVMVerifyFunction(ref, action) == 0
}
