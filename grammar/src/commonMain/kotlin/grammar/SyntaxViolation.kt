package com.lorenzoog.jplank.grammar

import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.message.MessageRenderer

data class SyntaxViolation(val message: String, val location: Location) {
  fun render(renderer: MessageRenderer) {
    renderer.severe(message, location)
  }
}

fun List<SyntaxViolation>.render(renderer: MessageRenderer) {
  forEach { it.render(renderer) }
}
