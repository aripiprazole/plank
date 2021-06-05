package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.analyzer.PlankType.Companion.pointer
import com.lorenzoog.plank.analyzer.Variable
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

sealed class TypedExpr : TypedPlankElement {
  interface Visitor<T> {
    fun visit(expr: TypedExpr): T = expr.accept(this)

    fun visitConstExpr(const: TypedConstExpr): T
    fun visitIfExpr(expr: TypedIfExpr): T
    fun visitAccessExpr(access: TypedAccessExpr): T
    fun visitCallExpr(call: TypedCallExpr): T
    fun visitAssignExpr(assign: TypedAssignExpr): T
    fun visitSetExpr(set: TypedSetExpr): T
    fun visitGetExpr(get: TypedGetExpr): T
    fun visitGroupExpr(group: TypedGroupExpr): T
    fun visitInstanceExpr(instance: TypedInstanceExpr): T
    fun visitSizeofExpr(sizeof: TypedSizeofExpr): T
    fun visitReferenceExpr(reference: TypedReferenceExpr): T
    fun visitValueExpr(value: TypedValueExpr): T
    fun visitMatchExpr(expr: TypedMatchExpr): T
  }

  abstract override val location: Location

  abstract fun <T> accept(visitor: Visitor<T>): T

  fun stmt(): TypedStmt = TypedExprStmt(this, location)
}

data class TypedConstExpr(
  val value: Any,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  val literal = value.toString()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitConstExpr(this)
  }
}

data class TypedIfExpr(
  val cond: TypedExpr,
  val thenBranch: TypedExpr,
  val elseBranch: TypedExpr?,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitIfExpr(this)
  }
}

data class TypedAccessExpr(val variable: Variable, override val location: Location) : TypedExpr() {
  val name = variable.name
  override val type = variable.value.type

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAccessExpr(this)
  }
}

data class TypedGroupExpr(val expr: TypedExpr, override val location: Location) : TypedExpr() {
  override val type = expr.type

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitGroupExpr(this)
  }
}

data class TypedAssignExpr(
  val name: Identifier,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAssignExpr(this)
  }
}

data class TypedSetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitSetExpr(this)
  }
}

data class TypedGetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitGetExpr(this)
  }
}

data class TypedCallExpr(
  val callee: TypedExpr,
  val arguments: List<TypedExpr>,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitCallExpr(this)
  }
}

data class TypedInstanceExpr(
  val arguments: Map<Identifier, TypedExpr>,
  override val type: PlankType,
  override val location: Location,
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitInstanceExpr(this)
  }
}

data class TypedSizeofExpr(
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitSizeofExpr(this)
  }
}

data class TypedReferenceExpr(
  val expr: TypedExpr,
  override val location: Location
) : TypedExpr() {
  override val type = pointer(expr.type)

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitReferenceExpr(this)
  }
}

data class TypedValueExpr(
  val expr: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitValueExpr(this)
  }
}

data class TypedMatchExpr(
  val subject: TypedExpr,
  val patterns: Map<TypedPattern, TypedExpr>,
  override val type: PlankType,
  override val location: Location
) : TypedExpr() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitMatchExpr(this)
  }
}
