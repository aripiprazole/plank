package org.plank.syntax.element

data class QualifiedPath(
  val fullPath: List<Identifier> = emptyList(),
  override val location: Location = Location.Generated,
) : PlankElement {
  constructor(vararg identifiers: Identifier, location: Location) :
    this(identifiers.toList(), location)

  constructor(identifier: Identifier) :
    this(listOf(identifier), identifier.location)

  constructor(stringPath: String, location: Location = Location.Generated) :
    this(stringPath.split(".").map(::Identifier), location)

  interface Visitor<T> {
    fun visitQualifiedPath(path: QualifiedPath): T
  }

  val text: String get() = fullPath.joinToString(".") { it.text }

  fun toIdentifier(): Identifier {
    return Identifier(text, location)
  }

  fun reversed(): QualifiedPath {
    return copy(fullPath = fullPath.reversed())
  }

  operator fun plus(other: QualifiedPath): QualifiedPath {
    return QualifiedPath(fullPath + other.fullPath, location)
  }

  operator fun plus(other: Identifier): QualifiedPath {
    return QualifiedPath(fullPath + other, location)
  }

  operator fun plus(other: String): QualifiedPath {
    return QualifiedPath(fullPath + other.toIdentifier(), location)
  }

  override fun toString(): String = "QualifiedPath $fullPath"
}

operator fun Identifier.plus(other: QualifiedPath): QualifiedPath {
  return QualifiedPath(text) + other
}

operator fun Identifier.plus(other: Identifier): QualifiedPath {
  return QualifiedPath(text) + other
}

fun List<Identifier>.toQualifiedPath(): QualifiedPath {
  return QualifiedPath(this)
}

fun QualifiedPath?.orEmpty(): QualifiedPath {
  return this ?: QualifiedPath()
}

fun Identifier.toQualifiedPath(): QualifiedPath {
  return QualifiedPath(text)
}

fun String.toQualifiedPath(): QualifiedPath {
  return QualifiedPath(this)
}
