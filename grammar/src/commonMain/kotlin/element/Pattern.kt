package com.gabrielleeg1.plank.grammar.element

sealed interface Pattern : PlankElement {
  interface Visitor<T> {
    fun visit(pattern: Pattern): T = when (pattern) {
      is NamedTuplePattern -> visitNamedTuplePattern(pattern)
      is IdentPattern -> visitIdentPattern(pattern)
    }

    fun visitNamedTuplePattern(pattern: NamedTuplePattern): T
    fun visitIdentPattern(pattern: IdentPattern): T

    fun visitPatterns(many: List<Pattern>): List<T> = many.map(::visit)
  }
}

data class NamedTuplePattern(
  val type: QualifiedPath,
  val fields: List<Pattern>,
  override val location: Location
) : Pattern

data class IdentPattern(val name: Identifier, override val location: Location) : Pattern
