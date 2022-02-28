package org.plank.syntax.element

data class QualifiedPath(
  val fullPath: List<Identifier> = emptyList(),
  override val loc: Loc = GeneratedLoc,
) : PlankElement {
  constructor(vararg identifiers: Identifier, loc: Loc) :
    this(identifiers.toList(), loc)

  constructor(identifier: Identifier) :
    this(listOf(identifier), identifier.loc)

  constructor(stringPath: String, loc: Loc = GeneratedLoc) :
    this(stringPath.split(".").map(::Identifier), loc)

  interface Visitor<T> {
    fun visitQualifiedPath(path: QualifiedPath): T
  }

  val text: String get() = fullPath.joinToString(".") { it.text }

  fun toIdentifier(): Identifier {
    return Identifier(text, loc)
  }

  fun reversed(): QualifiedPath {
    return copy(fullPath = fullPath.reversed())
  }

  operator fun plus(other: QualifiedPath): QualifiedPath {
    return QualifiedPath(fullPath + other.fullPath, loc)
  }

  operator fun plus(other: Identifier): QualifiedPath {
    return QualifiedPath(fullPath + other, loc)
  }

  operator fun plus(other: String): QualifiedPath {
    return QualifiedPath(fullPath + other.toIdentifier(), loc)
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
