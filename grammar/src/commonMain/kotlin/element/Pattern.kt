package com.lorenzoog.plank.grammar.element

sealed class Pattern : PlankElement {
  data class NamedTuple(
    val type: Identifier,
    val fields: List<Pattern>,
    override val location: Location
  ) : Pattern()

  data class Ident(val name: Identifier, override val location: Location) : Pattern()
}
