package com.lorenzoog.plank.grammar.id

import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

object LogicalId {
  fun gt(location: Location = Location.undefined()) = Identifier.of(">", location)
  fun gte(location: Location = Location.undefined()) = Identifier.of(">=", location)
  fun lt(location: Location = Location.undefined()) = Identifier.of("<", location)
  fun lte(location: Location = Location.undefined()) = Identifier.of("<=", location)
  fun eq(location: Location = Location.undefined()) = Identifier.of("==", location)
  fun neq(location: Location = Location.undefined()) = Identifier.of("!=", location)
}
