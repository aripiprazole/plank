package org.plank.analyzer.infer

import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.Expr
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.GroupExpr
import org.plank.syntax.element.IfExpr
import org.plank.syntax.element.InstanceExpr
import org.plank.syntax.element.MatchExpr
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.element.orEmpty

fun Infer.inferExpr(env: TyEnv, expr: Expr): Pair<Ty, Subst> = when (expr) {
  is SizeofExpr -> i32Ty to nullSubst()
  is InstanceExpr -> expr.type.ty().constructor() to nullSubst()
  is GroupExpr -> inferExpr(env, expr.value)
  is RefExpr -> inferExpr(env, expr.value).mapFirst { PtrTy(it) }
  is BlockExpr -> inferExpr(inferStmts(env, expr.stmts), expr.value ?: ConstExpr(Unit))

  is AccessExpr -> {
    val path = (expr.module.orEmpty() + expr.name)
    val scheme = env.lookup(path.text)
      ?: throw UnboundVar(path.toIdentifier())

    instantiate(scheme) to nullSubst()
  }

  is DerefExpr -> {
    val tv = fresh()
    val (t1, s1) = inferExpr(env, expr.value)
    val s2 = unify(t1 ap s1, PtrTy(tv))

    tv ap s2 to (s2 compose s1)
  }

  is AssignExpr -> {
    val tv = fresh()
    val (t1, s1) = inferExpr(env, expr.value)
    val s2 = unify(t1 ap s1, tv)

    tv ap s2 to (s2 compose s1)
  }

  is GetExpr -> {
    val tv = fresh()
    val (t1) = inferExpr(env, expr.receiver)
    val (name) = t1.ungeneralize()

    val (_, t2) = env.lookup("$name.${expr.property.text}") ?: throw UnboundVar(expr.property)
    val s1 = unify(t2, t1 arr tv)

    tv ap s1 to s1
  }

  is SetExpr -> {
    val tv = fresh()
    val (t1) = inferExpr(env, expr.receiver)
    val (name) = t1.ungeneralize()

    val (_, t2) = env.lookup("$name.${expr.property.text}") ?: throw UnboundVar(expr.property)
    val s1 = unify(t2, t1 arr tv)

    tv ap s1 to s1
  }

  is CallExpr -> {
    expr.arguments
      .ifEmpty { listOf(ConstExpr(Unit)) }
      .fold(inferExpr(env, expr.callee)) { (t1, s1), next ->
        val tv = fresh()
        val (t2, s2) = inferExpr(env, next)
        val s3 = unify(t1 ap s2, t2 arr tv)

        tv ap s3 to (s3 compose s2 compose s1)
      }
  }

  is ConstExpr -> when (expr.value) {
    is Boolean -> boolTy to nullSubst()
    is Unit -> unitTy to nullSubst()
    is Int -> i32Ty to nullSubst()
    is Short -> i16Ty to nullSubst()
    is Byte -> i8Ty to nullSubst()
    is Double -> doubleTy to nullSubst()
    is Float -> floatTy to nullSubst()
    is String -> strTy to nullSubst()
    else -> throw LitNotSupported(expr.value)
  }

  is IfExpr -> when (val elseBranch = expr.elseBranch) {
    null -> {
      val (t1, s1) = inferExpr(env, expr.cond)
      val s2 = unify(t1, boolTy)
      val (_, s3) = inferBranch(env, expr.thenBranch)

      unitTy to (s3 compose s2 compose s1)
    }

    else -> {
      val tv = fresh()
      val (t1, s1) = inferExpr(env, expr.cond)
      val s2 = unify(t1, boolTy)
      val (t2, s3) = inferBranch(env, expr.thenBranch)
      val s4 = unify(t2, tv)
      val (t3, s5) = inferBranch(env, elseBranch)
      val s6 = unify(t2, t3)
      val s7 = s4 compose s6

      tv ap s7 to (s7 compose s6 compose s5 compose s4 compose s3 compose s2 compose s1)
    }
  }

  is MatchExpr -> {
    val (t1, _) = inferExpr(env, expr.subject)

    when {
      expr.patterns.isEmpty() -> unitTy to nullSubst()
      else -> {
        val fst = expr.patterns.entries.first()

        expr
          .patterns.entries
          .fold(
            inferExpr(inferPattern(env, fst.key, t1), fst.value),
          ) { (t2, s2), (pattern, value) ->
            val (t3, s3) = inferExpr(inferPattern(env, pattern, t1), value)
            val s4 = unify(t3 ap s2, t2)

            t3 ap s4 to (s4 compose s3 compose s2)
          }
      }
    }
  }
}
