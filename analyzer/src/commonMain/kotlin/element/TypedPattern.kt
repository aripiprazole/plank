package org.plank.analyzer.element

import org.plank.analyzer.EnumMemberInfo
import org.plank.analyzer.MUndef
import org.plank.analyzer.Mono
import org.plank.syntax.element.ErrorPlankElement
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedPattern : TypedPlankElement {
  interface Visitor<T> {
    fun visitPattern(pattern: TypedPattern): T = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: TypedNamedTuplePattern): T
    fun visitIdentPattern(pattern: TypedIdentPattern): T
    fun visitViolatedPattern(pattern: TypedViolatedPattern): T

    fun visitPatterns(many: List<TypedPattern>): List<T> = many.map(::visitPattern)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedNamedTuplePattern(
  val properties: List<TypedPattern>,
  val info: EnumMemberInfo,
  override val type: Mono,
  override val location: Location
) : TypedPattern {
  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class TypedIdentPattern(
  val name: Identifier,
  override val type: Mono,
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
  override val type = MUndef

  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitViolatedPattern(this)
  }
}
