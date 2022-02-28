package org.plank.syntax.element

sealed interface IfBranch : SimplePlankElement

data class ThenBranch(val value: Expr, override val loc: Loc = GeneratedLoc) :
  IfBranch

data class BlockBranch(
  val stmts: List<Stmt>,
  val value: Expr?,
  override val loc: Loc = GeneratedLoc,
) : IfBranch
