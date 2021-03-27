package com.lorenzoog.jplank.element

class Identifier(val text: String, override val location: Location) : PlankElement {
  override fun toString(): String = "Identifier($text)"
}
