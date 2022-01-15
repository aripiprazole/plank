package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.message.CompilerLogger

actual data class BindingViolation actual constructor(
  actual val message: String,
  actual val arguments: List<Any?>
) {
  actual fun render(renderer: CompilerLogger) {
    // TODO
    renderer.severe(message.format(args = arguments.toTypedArray()))
  }
}
