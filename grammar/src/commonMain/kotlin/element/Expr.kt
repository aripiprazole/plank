package com.gabrielleeg1.plank.grammar.element

sealed interface Expr : PlankElement

data class MatchExpr(
  val subject: Expr,
  val patterns: Map<Pattern, Expr>,
  override val location: Location
) : Expr

data class IfExpr(
  val cond: Expr,
  val thenBranch: Expr,
  val elseBranch: Expr?,
  override val location: Location
) : Expr

data class ConstExpr(val value: Any, override val location: Location) : Expr {
  val literal = value.toString()
}

data class AccessExpr(val path: QualifiedPath, override val location: Location) : Expr

data class GroupExpr(val value: Expr, override val location: Location) : Expr

data class AssignExpr(
  val name: Identifier,
  val value: Expr,
  override val location: Location
) : Expr

data class SetExpr(
  val receiver: Expr,
  val property: Identifier,
  val value: Expr,
  override val location: Location
) : Expr

data class GetExpr(
  val receiver: Expr,
  val property: Identifier,
  override val location: Location
) : Expr

data class CallExpr(
  val callee: Expr,
  val arguments: List<Expr>,
  override val location: Location
) : Expr

data class InstanceExpr(
  val type: TypeRef,
  val arguments: Map<Identifier, Expr>,
  override val location: Location,
) : Expr

data class SizeofExpr(val type: TypeRef, override val location: Location) : Expr

data class RefExpr(val expr: Expr, override val location: Location) : Expr

data class DerefExpr(val ref: Expr, override val location: Location) : Expr

data class ErrorExpr(
  override val message: String,
  override val arguments: List<Any> = emptyList(),
) : Expr, ErrorPlankElement {
  override val location = Location.Generated
}
