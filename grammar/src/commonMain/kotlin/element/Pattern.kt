package com.gabrielleeg1.plank.grammar.element

sealed interface Pattern : PlankElement

data class NamedTuplePattern(
  val type: QualifiedPath,
  val fields: List<Pattern>,
  override val location: Location
) : Pattern

data class IdentPattern(val name: Identifier, override val location: Location) : Pattern
