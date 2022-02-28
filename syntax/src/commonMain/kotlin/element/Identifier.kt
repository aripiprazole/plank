package org.plank.syntax.element

data class Identifier(val text: String, override val loc: Loc = GeneratedLoc) :
  PlankElement {
  override fun toString(): String = ":$text"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as Identifier

    if (text != other.text) return false

    return true
  }

  override fun hashCode(): Int {
    return text.hashCode()
  }
}

fun Iterable<Identifier>.text(): Iterable<String> = map { it.text }

fun String.toIdentifier(): Identifier = Identifier(this)
