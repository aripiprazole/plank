@file:Suppress("NOTHING_TO_INLINE")

package com.gabrielleeg1.plank.compiler

import org.llvm4j.llvm4j.Value

class DebugCompilerContext(private val context: CompilerContext) {
  fun printf(message: String, vararg arguments: Value): Unit = with(context) {
    val logMessage = buildLogMessage(message)
    buildCall(runtime.printf, listOf(buildGlobalStringPtr(logMessage, "debug-str"), *arguments))
  }

  fun log(message: String) {
    print(buildLogMessage(message))
  }

  private fun buildLogMessage(message: String): String {
    val local = Thread.currentThread().stackTrace.getOrNull(3) ?: "Unknown source"

    return "\u001b[31m[$local] ==> $message\u001B[0m\n"
  }
}
