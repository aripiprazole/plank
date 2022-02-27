package org.plank.analyzer.checker

import org.plank.analyzer.TypeMismatch
import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.unify
import org.plank.analyzer.resolver.FunctionScope
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

      val s = unify(scope.returnTy, value.ty)
      if (scope.returnTy ap s != value.ty) {
        violate<ResolvedFunctionBody>(body, TypeMismatch(scope.returnTy ap s, value.ty))
      }

      ResolvedCodeBody(stmts, value, body.location)
    }
    is ExprBody -> {
      val value = checkExpr(body.expr)
      val scope = scope as FunctionScope

      val s = unify(value.ty, scope.returnTy)
      if (scope.returnTy != value.ty ap s) {
        violate<ResolvedFunctionBody>(body, TypeMismatch(scope.returnTy, value.ty ap s))
      }

      ResolvedExprBody(value, body.location)
    }
    is NoBody -> ResolvedNoBody(body.location)
  }
}
