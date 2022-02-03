package org.plank.analyzer

import org.plank.syntax.element.Location
import org.plank.syntax.message.CompilerLogger

data class BindingViolation(val message: String, val location: Location) {
  fun render(logger: CompilerLogger) {
    logger.severe(message, location)
  }

  override fun toString(): String = message
}
