package com.lorenzoog.plank.grammar.id

import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

object BinaryId {
  fun add(location: Location = Location.undefined()) = Identifier.of("+", location)
  fun sub(location: Location = Location.undefined()) = Identifier.of("-", location)
  fun times(location: Location = Location.undefined()) = Identifier.of("*", location)
  fun div(location: Location = Location.undefined()) = Identifier.of("/", location)
}
