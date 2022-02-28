package org.plank.syntax.element

sealed interface Expr : PlankElement

data class BlockExpr(
  val value: Expr? = null,
  val stmts: List<Stmt> = emptyList(),
  override val location: Location = Location.Generated,
) : Expr {
  constructor(vararg stmts: Stmt, value: Expr? = null, location: Location = Location.Generated) :
    this(value, stmts.toList(), location)
}

data class MatchExpr(
  val subject: Expr,
  val patterns: Map<Pattern, Expr>,
  override val location: Location = Location.Generated,
) : Expr

data class IfExpr(
  val cond: Expr,
  val thenBranch: IfBranch,
  val elseBranch: IfBranch?,
  override val location: Location = Location.Generated,
) : Expr

data class ConstExpr(val value: Any, override val location: Location = Location.Generated) : Expr

data class AccessExpr(
  val name: Identifier,
  val module: QualifiedPath? = null,
  override val location: Location = Location.Generated,
) : Expr

data class GroupExpr(val value: Expr, override val location: Location = Location.Generated) : Expr

data class AssignExpr(
  val name: Identifier,
  val value: Expr,
  val module: QualifiedPath? = null,
  override val location: Location = Location.Generated,
) : Expr

data class SetExpr(
  val receiver: Expr,
  val property: Identifier,
  val value: Expr,
  override val location: Location = Location.Generated,
) : Expr

data class GetExpr(
  val receiver: Expr,
  val property: Identifier,
  override val location: Location = Location.Generated,
) : Expr

data class CallExpr(
  val callee: Expr,
  val arguments: List<Expr>,
  override val location: Location = Location.Generated,
) : Expr {
  constructor(callee: Expr, vararg arguments: Expr, location: Location = Location.Generated) :
    this(callee, arguments.toList(), location)
}

data class InstanceExpr(
  val type: TypeRef,
  val arguments: Map<Identifier, Expr>,
  override val location: Location = Location.Generated,
) : Expr

data class SizeofExpr(val type: TypeRef, override val location: Location = Location.Generated) :
  Expr

data class RefExpr(val value: Expr, override val location: Location = Location.Generated) : Expr

data class DerefExpr(val value: Expr, override val location: Location = Location.Generated) : Expr
