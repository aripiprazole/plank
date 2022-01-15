package com.gabrielleeg1.plank.grammar.element

sealed interface Stmt : PlankElement

data class ExprStmt(val expr: Expr, override val location: Location) : Stmt

data class ReturnStmt(val value: Expr?, override val location: Location) : Stmt

data class ErrorStmt(
  override val message: String,
  override val arguments: List<Any>
) : Stmt, ErrorPlankElement {
  override val location = Location.Generated
}
