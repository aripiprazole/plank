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

  fun runInfer(expr: Expr): Hole<Type> {
    return when (expr) {
      is SizeofExpr -> Type.Int32
      is GroupExpr -> runInfer(expr.value)
      is ConstExpr -> when (expr.value) {
        is Int -> Type.Int32
        is Boolean -> Type.Bool
        is String -> Type.String
        else -> throw InferFail("unsupported constant ${expr.value}")
      }

      is AccessExpr -> currentScope.findVariable(expr.name.text)
        ?.asHole()
        ?: throw InferFail("unbound ${expr.name}")

      is CallExpr -> {
        expr.arguments
          .ifEmpty { listOf(ConstExpr(Unit)) }
          .fold(runInfer(expr.callee)) { callee, arg ->
            val rhs = fresh()
            val lhs = runInfer(arg)
            unify(callee, lhs arrow rhs)

            rhs
          }
      }

      is AssignExpr -> TODO()
      is BlockExpr -> TODO()

      is DerefExpr -> TODO()
      is GetExpr -> TODO()
      is IfExpr -> TODO()
      is InstanceExpr -> TODO()
      is MatchExpr -> TODO()
      is RefExpr -> TODO()
      is SetExpr -> TODO()
    }
  }

  private fun unify(lhs: Hole<Type>, rhs: Hole<Type>) {
    val lhsValue = lhs.unwrap()
    val rhsValue = rhs.unwrap()

    when {
      lhs == rhs -> return
      lhsValue is VarType -> lhs bind rhs
      rhsValue is VarType -> rhs bind lhs
      lhsValue is AppType && rhsValue is AppType -> {
        unify(lhsValue.lhs, rhsValue.lhs)
        unify(lhsValue.rhs, rhsValue.rhs)
      }

      else -> throw InferFail("can not unify $lhs and $rhs")
    }
  }

  private infix fun Hole<Type>.bind(other: Hole<Type>) {
    val ref = unwrapAs<VarType>()

    when {
      this == other -> return
      ref.name in other.unwrap().ftv() -> throw InferFail("infinite type ${ref.name} in $other")
      else -> overwrite(other)
    }
  }

  private fun fresh(): Hole<Type> = VarType(letters.elementAt(++state)).asHole()
}
