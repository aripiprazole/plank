package org.plank.grammar.mapper

import org.plank.grammar.element.Location
import org.plank.grammar.message.CompilerLogger

data class SyntaxViolation(
  override val message: String,
  val location: Location
) : RuntimeException() {
  fun render(logger: CompilerLogger) {
    logger.severe(message, location)
  }

  override fun toString(): String = message
}

fun List<SyntaxViolation>.render(renderer: CompilerLogger) {
  forEach { it.render(renderer) }
}
