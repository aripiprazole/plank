package org.plank.analyzer

import org.plank.grammar.element.Location
import org.plank.grammar.message.CompilerLogger

data class BindingViolation(val message: String, val location: Location) {
  fun render(logger: CompilerLogger) {
    logger.severe(message, location)
  }

  override fun toString(): String = message
}
