package com.gabrielleeg1.plank.grammar.element

class Identifier(val text: String, override val location: Location = Location.Generated) :
  PlankElement {
  interface Visitor<T> {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Replace with pattern matching")
    fun visit(identifier: Identifier): T = visitIdentifier(identifier)

    fun visitIdentifier(identifier: Identifier): T
  }

  override fun toString(): String = "Identifier($text)"

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
