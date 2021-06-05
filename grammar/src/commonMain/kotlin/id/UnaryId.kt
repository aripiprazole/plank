package com.lorenzoog.plank.grammar.id

import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

object UnaryId {
  fun plus(location: Location = Location.undefined()) = Identifier.of("+", location)
  fun neg(location: Location = Location.undefined()) = Identifier.of("-", location)
}
