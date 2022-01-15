package com.gabrielleeg1.plank.grammar.element

data class Identifier(val text: String, override val location: Location = Location.Generated) :
  PlankElement {
  interface Visitor<T> {
    fun visit(identifier: Identifier): T = visitIdentifier(identifier)

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

  companion object {
    fun eq(location: Location = Location.Generated): Identifier = Identifier("==", location)
    fun neq(location: Location = Location.Generated): Identifier = Identifier("!=", location)
    fun gt(location: Location = Location.Generated): Identifier = Identifier(">", location)
    fun gte(location: Location = Location.Generated): Identifier = Identifier(">=", location)
    fun lte(location: Location = Location.Generated): Identifier = Identifier("<=", location)
    fun lt(location: Location = Location.Generated): Identifier = Identifier("<", location)

    fun add(location: Location = Location.Generated): Identifier = Identifier("+", location)
    fun sub(location: Location = Location.Generated): Identifier = Identifier("-", location)
    fun times(location: Location = Location.Generated): Identifier = Identifier("*", location)
    fun div(location: Location = Location.Generated): Identifier = Identifier("/", location)

    fun concat(location: Location = Location.Generated): Identifier = Identifier("++", location)
  }
}
