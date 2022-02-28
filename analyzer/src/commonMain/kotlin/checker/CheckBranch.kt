package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedBlockBranch
import org.plank.analyzer.element.TypedIfBranch
import org.plank.analyzer.element.TypedThenBranch
import org.plank.analyzer.resolver.ClosureScope
import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.IfBranch
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.element.toIdentifier

fun TypeCheck.checkBranch(branch: IfBranch): TypedIfBranch = when (branch) {
  is ThenBranch -> TypedThenBranch(checkExpr(branch.value), branch.loc)
  is BlockBranch -> {
    scoped(ClosureScope("BlockBranch".toIdentifier(), branch.stmts, scope)) {
      val stmts = branch.stmts.map(::checkStmt)
      val value = checkExpr(branch.value ?: ConstExpr(Unit))

      TypedBlockBranch(stmts, value, references, branch.loc)
    }
  }
}
