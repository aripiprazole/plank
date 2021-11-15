package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.message.CompilerLogger

class BindingViolation(val message: String, val arguments: List<Any?>) {
  fun render(renderer: CompilerLogger) {
    TODO()
  }
}
