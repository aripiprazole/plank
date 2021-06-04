package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.grammar.element.Location
import org.antlr.v4.kotlinruntime.Token

sealed class TypedExpr : TypedPlankElement {
  interface Visitor<T> {
    fun visit(expr: TypedExpr): T = expr.accept(this)

    fun visitMatchExpr(match: Match): T
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
    fun visitSizeofExpr(sizeof: Sizeof): T
    fun visitReferenceExpr(reference: Reference): T
    fun visitValueExpr(value: Value): T
    fun visitConcatExpr(concat: Concat): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class Match(
    val subject: TypedExpr,
    val patterns: Map<TypedPattern, TypedExpr>,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitMatchExpr(this)
    }
  }

  data class If(
    val cond: TypedExpr,
    val thenBranch: List<TypedStmt>,
    val elseBranch: List<TypedStmt>,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitIfExpr(this)
    }
  }

  data class Const(val value: Any, override val location: Location) : TypedExpr() {
    val literal = value.toString()

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitConstExpr(this)
    }
  }

  data class Access(val name: TypedIdentifier, override val location: Location) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAccessExpr(this)
    }
  }

  data class Logical(
    val lhs: TypedExpr,
    val op: Operation,
    val rhs: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    enum class Operation { Equals, NotEquals, Greater, GreaterEquals, Less, LessEquals }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitLogicalExpr(this)
    }
  }

  data class Concat(
    val lhs: TypedExpr,
    val rhs: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitConcatExpr(this)
    }
  }

  data class Binary(
    val lhs: TypedExpr,
    val op: Operation,
    val rhs: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    enum class Operation { Add, Sub, Mul, Div }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitBinaryExpr(this)
    }
  }

  data class Unary(
    val op: Operation,
    val rhs: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    enum class Operation { Neg, Bang }

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitUnaryExpr(this)
    }
  }

  data class Group(
    val expr: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGroupExpr(this)
    }
  }

  data class Assign(
    val name: TypedIdentifier,
    val value: TypedExpr,
    override val location: Location
  ) : TypedExpr() {

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAssignExpr(this)
    }
  }

  data class Set(
    val receiver: TypedExpr,
    val member: TypedIdentifier,
    val value: TypedExpr,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitSetExpr(this)
    }
  }

  data class Get(
    val receiver: TypedExpr,
    val member: TypedIdentifier,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGetExpr(this)
    }
  }

  data class Call(
    val callee: TypedExpr,
    val arguments: List<TypedExpr>,
    override val location: Location
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitCallExpr(this)
    }
  }

  data class Instance(
    val name: TypedIdentifier,
    val arguments: Map<Token, TypedExpr>,
    override val location: Location,
  ) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitInstanceExpr(this)
    }
  }

  // todo change to typedef
  data class Sizeof(val name: TypedIdentifier, override val location: Location) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitSizeofExpr(this)
    }
  }

  data class Reference(val expr: TypedExpr, override val location: Location) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitReferenceExpr(this)
    }
  }

  data class Value(val expr: TypedExpr, override val location: Location) : TypedExpr() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitValueExpr(this)
    }
  }
}
