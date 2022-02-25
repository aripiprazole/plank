package org.plank.codegen

import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import llvm.LLVMFatalErrorHandler

val fatalErrorHandler: LLVMFatalErrorHandler = staticCFunction { reason ->
  handleFatalError(reason?.toKString() ?: "Unknown").toString()
}

actual fun setupDebugPretty() {
  llvm.LLVMResetFatalErrorHandler()
  llvm.LLVMInstallFatalErrorHandler(fatalErrorHandler)
  llvm.LLVMEnablePrettyStackTrace()
}
