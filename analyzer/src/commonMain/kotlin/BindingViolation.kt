package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.message.CompilerLogger

data class BindingViolation(val message: String, val location: Location) {
  fun render(logger: CompilerLogger) {
    logger.severe(message, location)
  }
}
