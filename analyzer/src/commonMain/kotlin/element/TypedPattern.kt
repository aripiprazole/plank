package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.grammar.element.Location

sealed class TypedPattern : TypedPlankElement {
  data class NamedTuple(
    val reference: TypedIdentifier,
    val fields: List<TypedPattern>,
    override val location: Location
  ) : TypedPattern()

  data class Ident(val name: TypedIdentifier, override val location: Location) : TypedPattern()
}
