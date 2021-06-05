package com.lorenzoog.plank.grammar.element

class Identifier(val text: String, override val location: Location) : PlankElement {
  override fun toString(): String = "Identifier($text)"

  companion object {
    fun of(text: String, location: Location = Location.undefined()): Identifier {
      return Identifier(text, location)
    }
  }
}
