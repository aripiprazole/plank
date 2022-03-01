package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.GeneratedLoc
import org.plank.syntax.element.Loc

sealed interface ResolvedFunctionBody : ResolvedPlankElement

data class ResolvedNoBody(override val loc: Loc = GeneratedLoc) :
  ResolvedFunctionBody

data class ResolvedExprBody(
  val expr: TypedExpr,
  override val loc: Loc = GeneratedLoc,
) : ResolvedFunctionBody, TypedPlankElement {
  override val ty: Ty = expr.ty

  override fun ap(subst: Subst): ResolvedExprBody = copy(expr = expr.ap(subst))
}

data class ResolvedCodeBody(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr?,
  override val loc: Loc = GeneratedLoc,
) : ResolvedFunctionBody {
  val hasReturnedUnit: Boolean
    get(): Boolean = value?.ty == unitTy ||
      stmts.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()
}
