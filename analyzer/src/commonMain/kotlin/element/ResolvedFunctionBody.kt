package org.plank.analyzer.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.UnitType
import org.plank.syntax.element.Location

sealed interface ResolvedFunctionBody : ResolvedPlankElement {
  interface Visitor<T> {
    fun visit(body: ResolvedFunctionBody): T = body.accept(this)

    fun visitNoBody(body: ResolvedNoBody): T
    fun visitExprBody(body: ResolvedExprBody): T
    fun visitCodeBody(body: ResolvedCodeBody): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ResolvedNoBody(override val location: Location) : ResolvedFunctionBody {
  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitNoBody(this)
  }
}

data class ResolvedExprBody(
  val expr: TypedExpr,
  override val location: Location
) : ResolvedFunctionBody, TypedPlankElement {
  override val type: PlankType get() = expr.type

  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitExprBody(this)
  }
}

data class ResolvedCodeBody(
  val stmts: List<ResolvedStmt>,
  val returned: TypedExpr?,
  override val location: Location
) : ResolvedFunctionBody {
  val hasReturnedUnit: Boolean
    get(): Boolean = returned?.type == UnitType ||
      stmts.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()

  override fun <T> accept(visitor: ResolvedFunctionBody.Visitor<T>): T {
    return visitor.visitCodeBody(this)
  }
}
