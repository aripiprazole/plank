package org.plank.syntax.element

sealed interface Stmt : PlankElement

data class ExprStmt(val expr: Expr, override val location: Location = Location.Generated) : Stmt

data class ReturnStmt(val value: Expr?, override val location: Location = Location.Generated) :
  Stmt
