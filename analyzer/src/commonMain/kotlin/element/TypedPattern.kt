package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import org.plank.syntax.element.QualifiedPath

sealed interface TypedPattern : TypedPlankElement {
  override fun ap(subst: Subst): TypedPattern
}

data class TypedEnumVariantPattern(
  val name: QualifiedPath,
  val properties: List<TypedPattern> = emptyList(),
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedPattern {
  override fun ap(subst: Subst): TypedEnumVariantPattern =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))
}

data class TypedIdentPattern(
  val name: Identifier,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedPattern {
  override fun ap(subst: Subst): TypedIdentPattern =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))
}
