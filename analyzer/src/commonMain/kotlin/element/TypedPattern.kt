package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import org.plank.syntax.element.QualifiedPath

sealed interface TypedPattern : TypedPlankElement {
  interface Visitor<T> {
    fun visitPattern(pattern: TypedPattern): T = pattern.accept(this)

    fun visitNamedTuplePattern(pattern: TypedNamedTuplePattern): T
    fun visitIdentPattern(pattern: TypedIdentPattern): T

    fun visitPatterns(many: List<TypedPattern>): List<T> = many.map(::visitPattern)
  }

  override fun ap(subst: Subst): TypedPattern

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedNamedTuplePattern(
  val name: QualifiedPath,
  val properties: List<TypedPattern> = emptyList(),
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedPattern {
  override fun ap(subst: Subst): TypedNamedTuplePattern =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitNamedTuplePattern(this)
  }
}

data class TypedIdentPattern(
  val name: Identifier,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedPattern {
  override fun ap(subst: Subst): TypedIdentPattern =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedPattern.Visitor<T>): T {
    return visitor.visitIdentPattern(this)
  }
}
