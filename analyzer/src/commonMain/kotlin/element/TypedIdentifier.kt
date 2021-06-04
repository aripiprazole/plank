package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.grammar.element.Location

class TypedIdentifier(val text: String, override val location: Location) : TypedPlankElement {
  override fun toString(): String = "Identifier($text)"
}
