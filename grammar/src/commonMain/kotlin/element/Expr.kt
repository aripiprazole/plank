package com.lorenzoog.plank.grammar.element

sealed class Expr : PlankElement {
  interface Visitor<T> {
    fun visit(expr: Expr): T = expr.accept(this)

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
    fun visitReferenceExpr(expr: RefExpr): T
    fun visitValueExpr(expr: DerefExpr): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class MatchExpr(
  val subject: Expr,
  val patterns: Map<Pattern, Expr>,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitMatchExpr(this)
  }
}

data class IfExpr(
  val cond: Expr,
  val thenBranch: Expr,
  val elseBranch: Expr?,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitIfExpr(this)
  }
}

data class ConstExpr(val value: Any, override val location: Location) : Expr() {
  val literal = value.toString()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitConstExpr(this)
  }
}

data class AccessExpr(val path: QualifiedPath, override val location: Location) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAccessExpr(this)
  }
}

data class GroupExpr(val expr: Expr, override val location: Location) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitGroupExpr(this)
  }
}

data class AssignExpr(
  val path: QualifiedPath,
  val value: Expr,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAssignExpr(this)
  }
}

data class SetExpr(
  val receiver: Expr,
  val property: Identifier,
  val value: Expr,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitSetExpr(this)
  }
}

data class GetExpr(
  val receiver: Expr,
  val property: Identifier,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitGetExpr(this)
  }
}

data class CallExpr(
  val callee: Expr,
  val arguments: List<Expr>,
  override val location: Location
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitCallExpr(this)
  }
}

data class InstanceExpr(
  val struct: TypeRef,
  val arguments: Map<Identifier, Expr>,
  override val location: Location,
) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitInstanceExpr(this)
  }
}

data class SizeofExpr(val type: TypeRef, override val location: Location) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitSizeofExpr(this)
  }
}

data class RefExpr(val expr: Expr, override val location: Location) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitReferenceExpr(this)
  }
}

data class DerefExpr(val expr: Expr, override val location: Location) : Expr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitValueExpr(this)
  }
}
