package com.lorenzoog.plank.grammar.mapper

import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.message.MessageRenderer

sealed class SyntaxViolation : RuntimeException() {
  abstract val location: Location

  abstract fun render(renderer: MessageRenderer)
}

data class ExpectingViolation(
  val expected: String,
  val actual: String,
  override val location: Location
) : SyntaxViolation() {
  override val message: String
    get() = "Expecting $expected, but found $actual"

  override fun render(renderer: MessageRenderer) {
    renderer.warning(message)
  }
}

data class RecognitionViolation(
  override val message: String,
  override val location: Location
) : SyntaxViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe(message, location)
  }
}

fun List<SyntaxViolation>.render(renderer: MessageRenderer) {
  forEach { it.render(renderer) }
}
