package org.plank.syntax.parsing

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
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.parser.PlankParser.AccessExprContext
import org.plank.syntax.parser.PlankParser.ArgContext
import org.plank.syntax.parser.PlankParser.AssignExprContext
import org.plank.syntax.parser.PlankParser.BinaryExprContext
import org.plank.syntax.parser.PlankParser.BlockExprContext
import org.plank.syntax.parser.PlankParser.CallArgContext
import org.plank.syntax.parser.PlankParser.CallExprContext
import org.plank.syntax.parser.PlankParser.DecimalExprContext
import org.plank.syntax.parser.PlankParser.DerefExprContext
import org.plank.syntax.parser.PlankParser.ExprContext
import org.plank.syntax.parser.PlankParser.FalseExprContext
import org.plank.syntax.parser.PlankParser.GetArgContext
import org.plank.syntax.parser.PlankParser.GroupExprContext
import org.plank.syntax.parser.PlankParser.IfExprContext
import org.plank.syntax.parser.PlankParser.InstanceExprContext
import org.plank.syntax.parser.PlankParser.IntExprContext
import org.plank.syntax.parser.PlankParser.MatchExprContext
import org.plank.syntax.parser.PlankParser.PrimaryContext
import org.plank.syntax.parser.PlankParser.RefExprContext
import org.plank.syntax.parser.PlankParser.SetExprContext
import org.plank.syntax.parser.PlankParser.SizeofExprContext
import org.plank.syntax.parser.PlankParser.StringExprContext
import org.plank.syntax.parser.PlankParser.TrueExprContext
import org.plank.syntax.parser.PlankParser.UnaryExprContext

fun PrimaryContext.exprToAst(file: PlankFile): Expr = when (this) {
  is RefExprContext -> RefExpr(value!!.exprToAst(file), treeLoc(file))
  is DerefExprContext -> DerefExpr(value!!.exprToAst(file), treeLoc(file))
  is IntExprContext -> ConstExpr(value!!.text!!.toInt(), treeLoc(file))
  is DecimalExprContext -> ConstExpr(value!!.text!!.toDouble(), treeLoc(file))
  is StringExprContext -> ConstExpr(
    value!!.text!!.substring(1, value!!.text!!.length - 1),
    treeLoc(file),
  )

  is AccessExprContext -> AccessExpr(value!!.tokenToAst(file), null, treeLoc(file))
  is TrueExprContext -> ConstExpr(true, treeLoc(file))
  is FalseExprContext -> ConstExpr(false, treeLoc(file))
  is GroupExprContext -> when (value) {
    null -> ConstExpr(Unit, treeLoc(file))
    else -> GroupExpr(value!!.exprToAst(file), treeLoc(file))
  }

  else -> error("Unsupported primary ${this::class.simpleName}")
}

fun ExprContext.exprToAst(file: PlankFile): Expr = when (this) {
  is CallExprContext -> findArg().fold(callee!!.exprToAst(file), callFold(file))

  is AssignExprContext -> {
    AssignExpr(name!!.tokenToAst(file), value!!.exprToAst(file), null, treeLoc(file))
  }

  is SizeofExprContext -> {
    SizeofExpr(type!!.typeRefToAst(file), treeLoc(file))
  }

  is BlockExprContext -> {
    BlockExpr(value?.exprToAst(file), findStmt().map { it.stmtToAst(file) }, treeLoc(file))
  }

  is BinaryExprContext -> CallExpr(
    callee = op!!.tokenToAst(file).idToExpr(),
    arguments = listOf(lhs!!.exprToAst(file), rhs!!.exprToAst(file)),
    loc = treeLoc(file),
  )

  is UnaryExprContext -> CallExpr(
    callee = op!!.tokenToAst(file).idToExpr(),
    arguments = listOf(rhs!!.exprToAst(file)),
    loc = treeLoc(file),
  )

  is InstanceExprContext -> InstanceExpr(
    type = type!!.typeRefToAst(file),
    arguments = findInstanceArg().associate { arg ->
      arg.name!!.tokenToAst(file) to arg.value!!.exprToAst(file)
    },
    loc = treeLoc(file),
  )

  is IfExprContext -> IfExpr(
    cond = cond!!.exprToAst(file),
    thenBranch = mainBranch!!.thenBranchToAst(file),
    elseBranch = otherwiseBranch?.elseBranchToAst(file),
    loc = treeLoc(file),
  )

  is MatchExprContext -> MatchExpr(
    subject = subject!!.exprToAst(file),
    patterns = findMatchPattern().associate {
      it.key!!.patternToAst(file) to it.value!!.exprToAst(file)
    },
    loc = treeLoc(file),
  )

  is SetExprContext -> {
    val property = findArg().fold(receiver!!.exprToAst(file), callFold(file))
      as? GetExpr ?: error("Receiver must be a GetExpr when setting up a variable")

    SetExpr(property.receiver, property.property, value!!.exprToAst(file), treeLoc(file))
  }

  else -> error("Unsupported expr ${this::class.simpleName}")
}

fun callFold(file: PlankFile) = fun(acc: Expr, next: ArgContext): Expr = when (next) {
  is GetArgContext -> GetExpr(acc, next.name!!.tokenToAst(file), next.treeLoc(file))
  is CallArgContext -> next.findExpr()
    .ifEmpty { return CallExpr(acc, emptyList(), next.treeLoc(file)) }
    .fold(acc) { callee, arg ->
      CallExpr(callee, listOf(arg.exprToAst(file)), arg.treeLoc(file))
    }

  else -> error("Unsupported arg ${next::class.simpleName}")
}
