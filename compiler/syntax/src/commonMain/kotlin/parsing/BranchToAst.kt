package org.plank.syntax.parsing

import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.IfBranch
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.parser.PlankParser.BlockElseBranchContext
import org.plank.syntax.parser.PlankParser.BlockThenBranchContext
import org.plank.syntax.parser.PlankParser.ElseBranchContext
import org.plank.syntax.parser.PlankParser.MainElseBranchContext
import org.plank.syntax.parser.PlankParser.MainThenBranchContext
import org.plank.syntax.parser.PlankParser.ThenBranchContext

fun ThenBranchContext.thenBranchToAst(file: PlankFile): IfBranch = when (this) {
  is MainThenBranchContext -> ThenBranch(value!!.exprToAst(file), treeLoc(file))
  is BlockThenBranchContext -> {
    BlockBranch(findStmt().map { it.stmtToAst(file) }, value?.exprToAst(file), treeLoc(file))
  }

  else -> error("Unsupported then branch ${this::class.simpleName}")
}

fun ElseBranchContext.elseBranchToAst(file: PlankFile): IfBranch = when (this) {
  is MainElseBranchContext -> ThenBranch(value!!.exprToAst(file), treeLoc(file))
  is BlockElseBranchContext -> {
    BlockBranch(findStmt().map { it.stmtToAst(file) }, value?.exprToAst(file), treeLoc(file))
  }

  else -> error("Unsupported else branch ${this::class.simpleName}")
}
