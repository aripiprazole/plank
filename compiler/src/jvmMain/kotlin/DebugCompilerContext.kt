@file:Suppress("NOTHING_TO_INLINE")

package com.lorenzoog.plank.compiler

import org.llvm4j.llvm4j.Value

class DebugCompilerContext(val context: CompilerContext) {
  inline fun printf(message: String, vararg arguments: Value): Unit = with(context) {
    val logMessage = "\u001b[31m[${this::class.simpleName}] ==> $message\n"

    buildCall(runtime.printf, listOf(buildGlobalStringPtr(logMessage, "debug-str"), *arguments))
  }

  inline fun log(message: String) {
    println("\u001b[31m[${this::class.simpleName}] ==> $message")
  }
}
