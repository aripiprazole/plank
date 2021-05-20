package com.lorenzoog.plank.grammar.mapper

import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.message.CompilerLogger

sealed class SyntaxViolation : RuntimeException() {
  abstract val location: Location

  abstract fun render(renderer: CompilerLogger)
}

data class ExpectingViolation(
  val expected: String,
  val actual: String,
  override val location: Location
) : SyntaxViolation() {
  override val message: String
    get() = "Expecting $expected, but found $actual"

  override fun render(renderer: CompilerLogger) {
    renderer.warning(message)
  }
}

data class RecognitionViolation(
  override val message: String,
  override val location: Location
) : SyntaxViolation() {
  override fun render(renderer: CompilerLogger) {
    renderer.severe(message, location)
  }
}

fun List<SyntaxViolation>.render(renderer: CompilerLogger) {
  forEach { it.render(renderer) }
}
