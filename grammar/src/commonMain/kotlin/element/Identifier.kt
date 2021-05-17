package com.lorenzoog.plank.grammar.element

class Identifier(val text: String, override val location: Location) : PlankElement {
  override fun toString(): String = "Identifier($text)"
}
