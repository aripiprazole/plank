package org.plank.syntax.element

data class QualifiedPath(val fullPath: List<Identifier>, override val location: Location) :
  PlankElement {
  constructor(identifier: Identifier) :
    this(listOf(identifier), identifier.location)

  constructor(stringPath: String, location: Location = Location.Generated) :
    this(stringPath.split(".").reversed().map(::Identifier), location)

  interface Visitor<T> {
    fun visit(path: QualifiedPath): T = visitQualifiedPath(path)

    fun visitQualifiedPath(path: QualifiedPath): T
  }

  val text: String get() = fullPath.joinToString(".") { it.text }

  fun toIdentifier(): Identifier {
    return Identifier(text, location)
  }
}
