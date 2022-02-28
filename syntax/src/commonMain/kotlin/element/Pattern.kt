package org.plank.syntax.element

sealed interface Pattern : SimplePlankElement

data class EnumVariantPattern(
  val type: QualifiedPath,
  val properties: List<Pattern>,
  override val loc: Loc,
) : Pattern {
  constructor(type: QualifiedPath, vararg properties: Pattern, loc: Loc = GeneratedLoc) :
    this(type, properties.toList(), loc)
}

data class IdentPattern(
  val name: Identifier,
  override val loc: Loc = GeneratedLoc,
) : Pattern
