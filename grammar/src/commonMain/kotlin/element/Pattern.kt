package com.lorenzoog.plank.grammar.element

sealed class Pattern : PlankElement {
  interface Visitor<T> {
    fun visit(pattern: Pattern) = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: NamedTuplePattern): T
    fun visitIdentPattern(pattern: IdentPattern): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class NamedTuplePattern(
  val type: QualifiedPath,
  val fields: List<Pattern>,
  override val location: Location
) : Pattern() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class IdentPattern(val name: Identifier, override val location: Location) : Pattern() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitIdentPattern(this)
  }
}
