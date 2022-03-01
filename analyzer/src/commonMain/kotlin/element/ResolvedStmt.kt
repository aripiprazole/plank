package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.Loc

sealed interface ResolvedStmt : ResolvedPlankElement

data class ResolvedExprStmt(val expr: TypedExpr, override val loc: Loc) :
  ResolvedStmt,
  TypedPlankElement {
  override val ty: Ty = expr.ty

  override fun ap(subst: Subst): ResolvedExprStmt = copy(expr = expr.ap(subst))
}

data class ResolvedReturnStmt(val value: TypedExpr?, override val loc: Loc) :
  ResolvedStmt,
  TypedPlankElement {
  override val ty: Ty = value?.ty ?: unitTy

  override fun ap(subst: Subst): ResolvedReturnStmt = copy(value = value?.ap(subst))
}
