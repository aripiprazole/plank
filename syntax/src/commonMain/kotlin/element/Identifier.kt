package org.plank.syntax.element

data class Identifier(val text: String, override val location: Location = Location.Generated) :
  PlankElement {
  interface Visitor<T> {
    fun visitIdentifier(identifier: Identifier): T
  }

  override fun toString(): String = "Identifier($text)"

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

fun String.toIdentifier(): Identifier = Identifier(this)
