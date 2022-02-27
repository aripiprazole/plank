package org.plank.analyzer.infer

import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.IfBranch
import org.plank.syntax.element.ThenBranch

fun Infer.inferBranch(env: TyEnv, branch: IfBranch): Pair<Ty, Subst> = when (branch) {
  is ThenBranch -> inferExpr(env, branch.value)
  is BlockBranch -> inferExpr(inferStmts(env, branch.stmts), branch.value ?: ConstExpr(Unit))
}
