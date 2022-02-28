package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc

sealed interface TypedIfBranch : TypedPlankElement {
  override fun ap(subst: Subst): TypedIfBranch
}

data class TypedThenBranch(
  val value: TypedExpr,
  override val loc: Loc,
) : TypedIfBranch {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedThenBranch = copy(value = value.ap(subst))
}

data class TypedBlockBranch(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr,
  val references: MutableMap<Identifier, Ty> = mutableMapOf(),
  override val loc: Loc,
) : TypedIfBranch {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedBlockBranch = copy(value = value.ap(subst))
}
