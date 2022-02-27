@file:Suppress("MemberVisibilityCanBePrivate")

package org.plank.analyzer.infer

import org.plank.syntax.element.Expr
import org.plank.syntax.element.Stmt

fun runInfer(expr: Expr, env: TyEnv = nullEnv()): Pair<Ty, Subst> {
  return with(Infer()) { inferExpr(env, expr) }
}

fun runInferStmt(stmt: Stmt, env: TyEnv = nullEnv()): TyEnv {
  return with(Infer()) { inferStmt(env, stmt) }
}

class Infer {
  private var state: Int = 0

  private val letters: Sequence<String> = sequence {
    var prefix = ""
    var i = 0
    while (true) {
      i++
      for (c in 'a'..'z') {
        yield("$prefix$c")
      }
      if (i > Char.MAX_VALUE.code) i = 0
      prefix += "${i.toChar()}"
    }
  }

  fun TyEnv.generalize(ty: Ty): Scheme {
    val ftv = ty.ftv().toMutableSet().also { it.removeAll(ftv()) }

    return Scheme(ftv, ty)
  }

  fun instantiate(scheme: Scheme): Ty {
    val map: Map<VarTy, Ty> = buildMap {
      scheme.names.forEach {
        getOrPut(VarTy(it)) { fresh() }
      }
    }

    return scheme.ty ap map.toSubst()
  }

  fun fresh(): Ty = VarTy(letters.elementAt(state)).also { state++ }
}
