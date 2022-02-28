package org.plank.syntax.mapper

import org.plank.syntax.element.Loc
import org.plank.syntax.message.CompilerLogger

data class SyntaxViolation(override val message: String, val loc: Loc) : RuntimeException() {
  fun render(logger: CompilerLogger) {
    logger.severe(message, loc)
  }

  override fun toString(): String = message
}

fun List<SyntaxViolation>.render(renderer: CompilerLogger) {
  forEach { it.render(renderer) }
}
