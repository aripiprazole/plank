package com.gabrielleeg1.plank.grammar.element

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
    return Identifier(text)
  }

  companion object {
    @Deprecated(
      message = "Replace with constructor calling",
      replaceWith = ReplaceWith("QualifiedPath(identifier)"),
      level = DeprecationLevel.ERROR,
    )
    fun from(identifier: Identifier): QualifiedPath {
      return QualifiedPath(identifier)
    }

    @Deprecated(
      message = "Replace with constructor calling",
      replaceWith = ReplaceWith("QualifiedPath(stringPath, location)"),
      level = DeprecationLevel.ERROR,
    )
    fun from(stringPath: String, location: Location = Location.Generated): QualifiedPath {
      return QualifiedPath(stringPath, location)
    }
  }
}
