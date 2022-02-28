package org.plank.syntax.element

sealed interface Pattern : PlankElement

data class EnumVariantPattern(
  val type: QualifiedPath,
  val properties: List<Pattern>,
  override val location: Location,
) : Pattern {
  constructor(
    type: QualifiedPath,
    vararg properties: Pattern,
    location: Location = Location.Generated,
  ) : this(type, properties.toList(), location)
}

data class IdentPattern(
  val name: Identifier,
  override val location: Location = Location.Generated,
) : Pattern
