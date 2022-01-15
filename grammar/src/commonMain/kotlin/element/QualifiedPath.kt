package com.gabrielleeg1.plank.grammar.element

data class QualifiedPath(val fullPath: List<Identifier>, override val location: Location) :
  PlankElement {
  constructor(identifier: Identifier) :
    this(listOf(identifier), identifier.location)

  constructor(stringPath: String, location: Location = Location.Generated) :
    this(stringPath.split(".").asReversed().map(::Identifier), location)

  val text: String get() = fullPath.joinToString(".")

  fun toIdentifier(): Identifier = Identifier(text)
}
