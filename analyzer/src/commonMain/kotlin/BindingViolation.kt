package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.message.CompilerLogger

expect class BindingViolation constructor(message: String, arguments: List<Any?>) {
  val message: String
  val arguments: List<Any?>

  fun render(renderer: CompilerLogger)
}
