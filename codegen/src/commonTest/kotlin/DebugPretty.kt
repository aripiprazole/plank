package org.plank.codegen

private var enabled: Boolean = false

expect fun setupDebugPretty()

fun handleFatalError(reason: String) {
  error("LLVM Fatal error: $reason")
}

fun installDebugPretty() {
  if (enabled) return
  enabled = true

  setupDebugPretty()
}
