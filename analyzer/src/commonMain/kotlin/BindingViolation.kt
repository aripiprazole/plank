package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.grammar.message.CompilerLogger

class BindingViolation(val message: String, val arguments: List<Any?>) {
  fun render(renderer: CompilerLogger) {
    TODO()
  }
}
