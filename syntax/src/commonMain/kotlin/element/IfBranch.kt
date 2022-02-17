package org.plank.syntax.element

sealed interface IfBranch : PlankElement {
  interface Visitor<T> {
    fun visitIfBranch(branch: IfBranch): T = branch.accept(this)
    fun visitThenBranch(branch: ThenBranch): T
    fun visitBlockBranch(branch: BlockBranch): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ThenBranch(val value: Expr, override val location: Location) : IfBranch {
  override fun <T> accept(visitor: IfBranch.Visitor<T>): T {
    return visitor.visitThenBranch(this)
  }
}

data class BlockBranch(val stmts: List<Stmt>, val value: Expr?, override val location: Location) :
  IfBranch {
  override fun <T> accept(visitor: IfBranch.Visitor<T>): T {
    return visitor.visitBlockBranch(this)
  }
}
