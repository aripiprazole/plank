package com.lorenzoog.plank.grammar.element

sealed class Expr : PlankElement {
  interface Visitor<T> {
    fun visit(expr: Expr): T = expr.accept(this)

    fun visitMatchExpr(match: Match): T
    fun visitIfExpr(anIf: If): T
    fun visitConstExpr(const: Const): T
    fun visitAccessExpr(access: Access): T
    fun visitCallExpr(call: Call): T
    fun visitAssignExpr(assign: Assign): T
    fun visitSetExpr(set: Set): T
    fun visitGetExpr(get: Get): T
    fun visitGroupExpr(group: Group): T
    fun visitInstanceExpr(instance: Instance): T
    fun visitSizeofExpr(sizeof: Sizeof): T
    fun visitReferenceExpr(reference: Reference): T
    fun visitValueExpr(value: Value): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class Match(
    val subject: Expr,
    val patterns: Map<Pattern, Expr>,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitMatchExpr(this)
    }
  }

  data class If(
    val cond: Expr,
    val thenBranch: Expr,
    val elseBranch: Expr?,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitIfExpr(this)
    }
  }

  data class Const(val value: Any, override val location: Location) : Expr() {
    val literal = value.toString()

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitConstExpr(this)
    }
  }

  data class Access(val name: Identifier, override val location: Location) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAccessExpr(this)
    }
  }

  data class Group(
    val expr: Expr,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGroupExpr(this)
    }
  }

  data class Assign(
    val name: Identifier,
    val value: Expr,
    override val location: Location
  ) : Expr() {

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAssignExpr(this)
    }
  }

  data class Set(
    val receiver: Expr,
    val property: Identifier,
    val value: Expr,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitSetExpr(this)
    }
  }

  data class Get(
    val receiver: Expr,
    val property: Identifier,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGetExpr(this)
    }
  }

  data class Call(
    val callee: Expr,
    val arguments: List<Expr>,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitCallExpr(this)
    }
  }

  data class Instance(
    val struct: TypeReference,
    val arguments: Map<Identifier, Expr>,
    override val location: Location,
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitInstanceExpr(this)
    }
  }

  // todo change to typedef
  data class Sizeof(val type: TypeReference, override val location: Location) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitSizeofExpr(this)
    }
  }

  data class Reference(val expr: Expr, override val location: Location) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitReferenceExpr(this)
    }
  }

  data class Value(val expr: Expr, override val location: Location) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitValueExpr(this)
    }
  }
}
