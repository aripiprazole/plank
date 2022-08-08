package org.plank.syntax.element

sealed interface Stmt : SimplePlankElement

data class ExprStmt(val expr: Expr, override val loc: Loc = GeneratedLoc) : Stmt

data class ReturnStmt(val value: Expr?, override val loc: Loc = GeneratedLoc) :
  Stmt
