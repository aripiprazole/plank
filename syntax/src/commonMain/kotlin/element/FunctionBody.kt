package org.plank.syntax.element

sealed interface FunctionBody : SimplePlankElement

data class NoBody(override val loc: Loc = GeneratedLoc) : FunctionBody

data class ExprBody(val expr: Expr, override val loc: Loc = GeneratedLoc) :
  FunctionBody

data class CodeBody(
  val stmts: List<Stmt>,
  val value: Expr?,
  override val loc: Loc = GeneratedLoc,
) : FunctionBody
