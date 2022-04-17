package org.plank.analyzer.infer

import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.NoBody

fun Infer.inferFunctionBody(env: TyEnv, body: FunctionBody): Pair<Ty, Subst> = when (body) {
  is ExprBody -> inferExpr(env, body.expr)
  is CodeBody -> inferExpr(inferStmts(env, body.stmts), body.value ?: ConstExpr(Unit))
  is NoBody -> fresh() to nullSubst()
}
