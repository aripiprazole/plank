package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.Location

sealed interface ResolvedFunctionBody : ResolvedPlankElement {
  interface Visitor<T> {
    fun visitFunctionBody(body: ResolvedFunctionBody): T = body.accept(this)

    fun visitNoBody(body: ResolvedNoBody): T
    fun visitExprBody(body: ResolvedExprBody): T
    fun visitCodeBody(body: ResolvedCodeBody): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ResolvedNoBody(override val location: Location = Location.Generated) :
  ResolvedFunctionBody {
  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitNoBody(this)
  }
}

data class ResolvedExprBody(
  val expr: TypedExpr,
  override val location: Location = Location.Generated,
) : ResolvedFunctionBody, TypedPlankElement {
  override val ty: Ty = expr.ty
  override val subst: Subst = expr.subst

  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitExprBody(this)
  }
}

data class ResolvedCodeBody(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr?,
  override val location: Location = Location.Generated,
) : ResolvedFunctionBody {
  val hasReturnedUnit: Boolean
    get(): Boolean = value?.ty == unitTy ||
      stmts.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()

  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitCodeBody(this)
  }
}
