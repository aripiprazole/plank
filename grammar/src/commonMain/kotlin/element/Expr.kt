package com.gabrielleeg1.plank.grammar.element

sealed interface Expr : PlankElement {
  interface Visitor<T> {
    fun visit(expr: Expr): T = when (expr) {
      is MatchExpr -> visitMatchExpr(expr)
      is IfExpr -> visitIfExpr(expr)
      is ConstExpr -> visitConstExpr(expr)
      is AccessExpr -> visitAccessExpr(expr)
      is GroupExpr -> visitGroupExpr(expr)
      is AssignExpr -> visitAssignExpr(expr)
      is SetExpr -> visitSetExpr(expr)
      is GetExpr -> visitGetExpr(expr)
      is CallExpr -> visitCallExpr(expr)
      is InstanceExpr -> visitInstanceExpr(expr)
      is SizeofExpr -> visitSizeofExpr(expr)
      is RefExpr -> visitRefExpr(expr)
      is DerefExpr -> visitDerefExpr(expr)
      is ErrorExpr -> visitErrorExpr(expr)
    }

    fun visitMatchExpr(expr: MatchExpr): T
    fun visitIfExpr(expr: IfExpr): T
    fun visitConstExpr(expr: ConstExpr): T
    fun visitAccessExpr(expr: AccessExpr): T
    fun visitCallExpr(expr: CallExpr): T
    fun visitAssignExpr(expr: AssignExpr): T
    fun visitSetExpr(expr: SetExpr): T
    fun visitGetExpr(expr: GetExpr): T
    fun visitGroupExpr(expr: GroupExpr): T
    fun visitInstanceExpr(expr: InstanceExpr): T
    fun visitSizeofExpr(expr: SizeofExpr): T
    fun visitRefExpr(expr: RefExpr): T
    fun visitDerefExpr(expr: DerefExpr): T
    fun visitErrorExpr(expr: ErrorExpr): T

    fun visitExprs(many: List<Expr>): List<T> = many.map(::visit)
  }
}

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
