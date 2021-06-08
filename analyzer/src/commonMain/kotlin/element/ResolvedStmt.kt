package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.element.PlankElement

abstract class ResolvedStmt internal constructor() : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: ResolvedStmt): T = stmt.accept(this)

    fun visitExprStmt(stmt: ResolvedExprStmt): T
    fun visitReturnStmt(stmt: ResolvedReturnStmt): T

    fun visitImportDecl(decl: ResolvedImportDecl): T
    fun visitModuleDecl(decl: ResolvedModuleDecl): T
    fun visitEnumDecl(decl: ResolvedEnumDecl): T
    fun visitStructDecl(decl: ResolvedStructDecl): T
    fun visitFunDecl(decl: ResolvedFunDecl): T
    fun visitLetDecl(decl: ResolvedLetDecl): T

    fun visitViolatedStmt(stmt: ViolatedStmt): T
    fun visitViolatedDecl(stmt: ViolatedDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class ResolvedExprStmt(val expr: TypedExpr, override val location: Location) :
  ResolvedStmt(),
  TypedPlankElement {
  override val type = expr.type

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitExprStmt(this)
  }
}

data class ResolvedReturnStmt(val value: TypedExpr?, override val location: Location) :
  ResolvedStmt(),
  TypedPlankElement {
  override val type = value?.type ?: PlankType.unit

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitReturnStmt(this)
  }
}

data class ViolatedStmt(
  override val message: String,
  override val arguments: List<Any>,
) : ResolvedStmt(), ViolatedPlankElement {
  override val location = Location.undefined()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitViolatedStmt(this)
  }
}
