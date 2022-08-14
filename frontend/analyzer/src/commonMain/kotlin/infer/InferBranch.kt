package org.plank.analyzer.infer

import org.plank.analyzer.element.TypedBlockBranch
import org.plank.analyzer.element.TypedIfBranch
import org.plank.analyzer.element.TypedThenBranch
import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.IfBranch
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.element.toIdentifier

fun TypeCheck.inferBranch(branch: IfBranch): TypedIfBranch = when (branch) {
  is ThenBranch -> TypedThenBranch(inferExpr(branch.value), branch.loc)
  is BlockBranch -> {
    scoped(ClosureScope("BlockBranch".toIdentifier(), scope)) {
      val stmts = branch.stmts.map(::inferStmt)
      val value = inferExpr(branch.value ?: ConstExpr(Unit))

      TypedBlockBranch(stmts, value, references, branch.loc)
    }
  }
}
