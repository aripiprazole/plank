package com.lorenzoog.jplank.element

import org.antlr.v4.kotlinruntime.Token

sealed class Expr : PkElement {
  interface Visitor<T> {
    fun visit(expr: Expr): T = expr.accept(this)

    fun visitIfExpr(anIf: If): T
    fun visitConstExpr(const: Const): T
    fun visitAccessExpr(access: Access): T
    fun visitLogicalExpr(logical: Logical): T
    fun visitBinaryExpr(binary: Binary): T
    fun visitUnaryExpr(unary: Unary): T
    fun visitCallExpr(call: Call): T
    fun visitAssignExpr(assign: Assign): T
    fun visitSetExpr(set: Set): T
    fun visitGetExpr(get: Get): T
    fun visitGroupExpr(group: Group): T
    fun visitInstanceExpr(instance: Instance): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class If(
    val cond: Expr,
    val thenBranch: List<Stmt>,
    val elseBranch: List<Stmt>,
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

  data class Access(val name: Token, override val location: Location) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAccessExpr(this)
    }
  }

  data class Logical(
    val lhs: Expr,
    val op: Operation,
    val rhs: Expr,
    override val location: Location
  ) : Expr() {
    enum class Operation { Equals, NotEquals, Greater, GreaterEquals, Less, LessEquals }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitLogicalExpr(this)
    }
  }

  data class Binary(
    val lhs: Expr,
    val op: Operation,
    val rhs: Expr,
    override val location: Location
  ) : Expr() {
    enum class Operation { Add, Sub, Mul, Div }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitBinaryExpr(this)
    }
  }

  data class Unary(
    val op: Operation,
    val rhs: Expr,
    override val location: Location
  ) : Expr() {
    enum class Operation { Neg, Bang }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitUnaryExpr(this)
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
    val name: Token,
    val value: Expr,
    override val location: Location
  ) : Expr() {

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAssignExpr(this)
    }
  }

  data class Set(
    val receiver: Expr,
    val member: Token,
    val value: Expr,
    override val location: Location
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitSetExpr(this)
    }
  }

  data class Get(
    val receiver: Expr,
    val member: Token,
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
    val name: Token,
    val arguments: Map<Token, Expr>,
    override val location: Location,
  ) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitInstanceExpr(this)
    }
  }
}
