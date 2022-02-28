package org.plank.syntax.element

sealed interface FunctionBody : PlankElement

data class NoBody(override val location: Location = Location.Generated) : FunctionBody

data class ExprBody(val expr: Expr, override val location: Location = Location.Generated) :
  FunctionBody

data class CodeBody(
  val stmts: List<Stmt>,
  val value: Expr?,
  override val location: Location = Location.Generated,
) : FunctionBody
