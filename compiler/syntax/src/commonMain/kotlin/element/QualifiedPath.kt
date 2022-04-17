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

  fun first(): Identifier = fullPath.first()
  fun last(): Identifier = fullPath.last()

  fun dropLast(): QualifiedPath = QualifiedPath(fullPath.dropLast(1), loc)

  fun reversed(): QualifiedPath = copy(fullPath = fullPath.reversed())

  operator fun plus(other: QualifiedPath): QualifiedPath =
    QualifiedPath(fullPath + other.fullPath, loc)

  operator fun plus(other: Identifier): QualifiedPath =
    QualifiedPath(fullPath + other, loc)

  operator fun plus(other: String): QualifiedPath =
    QualifiedPath(fullPath + other.toIdentifier(), loc)

  fun toIdentifier(): Identifier = Identifier(text, loc)

  override fun toString(): String = "QualifiedPath $fullPath"
}

fun QualifiedPath?.orEmpty(): QualifiedPath = this ?: QualifiedPath()

operator fun Identifier.plus(other: QualifiedPath): QualifiedPath = QualifiedPath(text) + other
operator fun Identifier.plus(other: Identifier): QualifiedPath = QualifiedPath(text) + other

fun List<Identifier>.toQualifiedPath(): QualifiedPath = QualifiedPath(this)
fun Identifier.toQualifiedPath(): QualifiedPath = QualifiedPath(text)
fun String.toQualifiedPath(): QualifiedPath = QualifiedPath(this)
