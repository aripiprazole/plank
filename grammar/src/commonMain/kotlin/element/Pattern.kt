package com.lorenzoog.plank.grammar.element

sealed class Pattern : PlankElement {
  interface Visitor<T> {
    fun visit(pattern: Pattern) = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: NamedTuple): T
    fun visitIdentPattern(pattern: Ident): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class NamedTuple(
    val type: Identifier,
    val fields: List<Pattern>,
    override val location: Location
  ) : Pattern() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitNamedTuplePattern(this)
    }
  }

  data class Ident(val name: Identifier, override val location: Location) : Pattern() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitIdentPattern(this)
    }
  }
}
