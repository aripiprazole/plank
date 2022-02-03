package org.plank.grammar.element

sealed interface Pattern : PlankElement {
  interface Visitor<T> {
    fun visit(pattern: Pattern): T = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: NamedTuplePattern): T
    fun visitIdentPattern(pattern: IdentPattern): T

    fun visitPatterns(many: List<Pattern>): List<T> = many.map(::visit)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class NamedTuplePattern(
  val type: QualifiedPath,
  val fields: List<Pattern>,
  override val location: Location
) : Pattern {
  override fun <T> accept(visitor: Pattern.Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class IdentPattern(val name: Identifier, override val location: Location) : Pattern {
  override fun <T> accept(visitor: Pattern.Visitor<T>): T {
    return visitor.visitIdentPattern(this)
  }
}
