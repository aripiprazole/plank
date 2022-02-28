package org.plank.syntax.element

sealed interface IfBranch : PlankElement

data class ThenBranch(val value: Expr, override val location: Location = Location.Generated) :
  IfBranch

data class BlockBranch(
  val stmts: List<Stmt>,
  val value: Expr?,
  override val location: Location = Location.Generated,
) : IfBranch
