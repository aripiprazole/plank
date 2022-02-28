package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.Location

sealed interface ResolvedFunctionBody : ResolvedPlankElement

data class ResolvedNoBody(override val location: Location = Location.Generated) :
  ResolvedFunctionBody

data class ResolvedExprBody(
  val expr: TypedExpr,
  override val location: Location = Location.Generated,
) : ResolvedFunctionBody, TypedPlankElement {
  override val ty: Ty = expr.ty
  override val subst: Subst = expr.subst

  override fun ap(subst: Subst): ResolvedExprBody = copy(expr = expr.ap(subst))
}

data class ResolvedCodeBody(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr?,
  override val location: Location = Location.Generated,
) : ResolvedFunctionBody {
  val hasReturnedUnit: Boolean
    get(): Boolean = value?.ty == unitTy ||
      stmts.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()
}
