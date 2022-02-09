package org.plank.analyzer.phases

import org.plank.analyzer.FunctionType
import org.plank.analyzer.UnitType
import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedExpr

object InliningPhase : IrTransformingPhase() {
  override fun transformCallExpr(expr: TypedCallExpr): TypedExpr {
    val type = expr.callee.type.unsafeCast<FunctionType>()
    val inlineCall = type.inlineCall ?: return expr

    if (!type.isInline) return expr
    if (type.isPartialApplied) return expr

    return when (val body = inlineCall(expr.arguments)) {
      is ResolvedCodeBody -> TypedBlockExpr(
        body.stmts, body.returned!!,
        type = body.returned.type,
        location = expr.location,
      )
      is ResolvedExprBody -> body.expr
      is ResolvedNoBody -> UnitType.const()
      else -> expr
    }
  }
}
