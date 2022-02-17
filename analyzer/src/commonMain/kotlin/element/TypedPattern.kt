package org.plank.analyzer.element

import org.plank.analyzer.infer.EnumMemberInfo
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedPattern : TypedPlankElement {
  interface Visitor<T> {
    fun visitPattern(pattern: TypedPattern): T = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: TypedNamedTuplePattern): T
    fun visitIdentPattern(pattern: TypedIdentPattern): T

    fun visitPatterns(many: List<TypedPattern>): List<T> = many.map(::visitPattern)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedNamedTuplePattern(
  val properties: List<TypedPattern>,
  val info: EnumMemberInfo,
  override val ty: Ty,
  override val location: Location,
) : TypedPattern {
  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class TypedIdentPattern(
  val name: Identifier,
  override val ty: Ty,
  override val location: Location,
) : TypedPattern {
  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitIdentPattern(this)
  }
}
