package org.plank.analyzer.phases

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedExpr

class ReifyTypesPhase : IrTransformingPhase() {
  override fun transformAccessExpr(expr: TypedAccessExpr): TypedExpr {
    return expr
  }
}
