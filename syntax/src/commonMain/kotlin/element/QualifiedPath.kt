package org.plank.syntax.element

data class QualifiedPath(
  val fullPath: List<Identifier>,
  override val location: Location = Location.Generated
) : PlankElement {
  constructor(identifier: Identifier) :
    this(listOf(identifier), identifier.location)

  constructor(stringPath: String, location: Location = Location.Generated) :
    this(stringPath.split(".").reversed().map(::Identifier), location)

  interface Visitor<T> {
    fun visitQualifiedPath(path: QualifiedPath): T
  }

  val text: String get() = fullPath.joinToString(".") { it.text }

  fun toIdentifier(): Identifier {
    return Identifier(text, location)
  }
}
