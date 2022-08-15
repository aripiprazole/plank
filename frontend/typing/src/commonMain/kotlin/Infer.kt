package org.plank.typing

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

class Infer {
  var currentScope = Scope()

  private var currentSubst: Subst = emptySubst()

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

  fun runInfer(expr: Expr): Type {
    return when (expr) {
      is SizeofExpr -> Type.Int32
      is ConstExpr -> when (expr.value) {
        is Int -> Type.Int32
        is Boolean -> Type.Bool
        is String -> Type.String
        else -> throw InferFail("unsupported constant ${expr.value}")
      }

      is AccessExpr -> currentScope.findVariable(expr.name.text)
        ?: throw InferFail("unbound ${expr.name}")

      is CallExpr -> {
        expr.arguments
          .ifEmpty { listOf(ConstExpr(Unit)) }
          .fold(runInfer(expr.callee)) { t1, arg ->
            val tv = fresh()
            val t2 = runInfer(arg)
            unify(t1, t2 arrow tv)

            tv.applySubst()
          }
      }

      is AssignExpr -> TODO()
      is BlockExpr -> TODO()

      is DerefExpr -> TODO()
      is GetExpr -> TODO()
      is GroupExpr -> TODO()
      is IfExpr -> TODO()
      is InstanceExpr -> TODO()
      is MatchExpr -> TODO()
      is RefExpr -> TODO()
      is SetExpr -> TODO()
    }
  }

  fun unify(t1: Type, t2: Type) {
    currentSubst = currentSubst compose mgu(t1, t2)
  }

  fun mgu(t1: Type, t2: Type): Subst = when {
    t1 == t2 -> emptySubst()
    t1 is VarType -> t1 bind t2
    t2 is VarType -> t2 bind t1
    t1 is AppType && t2 is AppType -> {
      val s1 = mgu(t1.lhs, t2.lhs)
      val s2 = mgu(t1.rhs apply s1, t2.rhs apply s1)

      s1 compose s2
    }

    else -> throw InferFail("can not unify $t1 and $t2")
  }

  infix fun VarType.bind(other: Type): Subst = when {
    this == other -> emptySubst()
    name in other.ftv() -> throw InferFail("infinite type $name in $other")
    else -> substOf(name to other)
  }

  fun fresh(): Type = VarType(letters.elementAt(++state))

  fun Type.applySubst(): Type = apply(currentSubst)
}
