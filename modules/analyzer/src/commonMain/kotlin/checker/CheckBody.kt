package org.plank.analyzer.checker

import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.NoBody

fun TypeCheck.checkBody(body: FunctionBody): ResolvedFunctionBody {
  return when (body) {
    is CodeBody -> {
      val stmts = body.stmts.map(::checkStmt)
      val value = checkExpr(body.value ?: ConstExpr(Unit))
      val scope = scope as FunctionScope

      if (scope.returnTy != value.ty) {
        violate<ResolvedFunctionBody>(body, TypeMismatch(scope.returnTy, value.ty))
      }

      ResolvedCodeBody(stmts, value, body.loc)
    }
    is ExprBody -> {
      val value = checkExpr(body.expr)
      val scope = scope as FunctionScope

      if (scope.returnTy != value.ty) {
        violate<ResolvedFunctionBody>(body, TypeMismatch(scope.returnTy, value.ty))
      }

      ResolvedExprBody(value, body.loc)
    }
    is NoBody -> ResolvedNoBody(body.loc)
  }
}
