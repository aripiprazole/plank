@file:JvmName("DebugPrettyJvm")

package org.plank.codegen

import org.bytedeco.llvm.global.LLVM

actual fun setupDebugPretty() {
  LLVM.LLVMResetFatalErrorHandler()
  LLVM.LLVMEnablePrettyStackTrace()
}
