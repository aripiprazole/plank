package org.plank.analyzer.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.StructType
import org.plank.analyzer.Untyped
import org.plank.syntax.element.ErrorPlankElement
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedPattern : TypedPlankElement {
  interface Visitor<T> {
    fun visit(pattern: TypedPattern): T = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: TypedNamedTuplePattern): T
    fun visitIdentPattern(pattern: TypedIdentPattern): T
    fun visitViolatedPattern(pattern: TypedViolatedPattern): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedNamedTuplePattern(
  val properties: List<TypedPattern>,
  override val type: StructType,
  override val location: Location
) : TypedPattern {
  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class TypedIdentPattern(
  val name: Identifier,
  override val type: PlankType,
  override val location: Location
) : TypedPattern {
  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitIdentPattern(this)
  }
}

data class TypedViolatedPattern(
  override val message: String,
  override val arguments: List<Any> = emptyList(),
  override val location: Location = Location.Generated,
) : TypedPattern, ErrorPlankElement {
  override val type = Untyped

  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitViolatedPattern(this)
  }
}
