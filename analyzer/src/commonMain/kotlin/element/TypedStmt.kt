package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.element.PlankElement

abstract class TypedStmt internal constructor() : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: TypedStmt): T = stmt.accept(this)

    fun visitExprStmt(stmt: TypedExprStmt): T
    fun visitReturnStmt(stmt: TypedReturnStmt): T

    fun visitImportDecl(decl: TypedImportDecl): T
    fun visitModuleDecl(decl: TypedModuleDecl): T
    fun visitEnumDecl(decl: TypedEnumDecl): T
    fun visitStructDecl(decl: TypedStructDecl): T
    fun visitFunDecl(decl: TypedFunDecl): T
    fun visitLetDecl(decl: TypedLetDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class TypedExprStmt(val expr: TypedExpr, override val location: Location) :
  TypedStmt(),
  TypedPlankElement {
  override val type = expr.type

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitExprStmt(this)
  }
}

data class TypedReturnStmt(val value: TypedExpr?, override val location: Location) :
  TypedStmt(),
  TypedPlankElement {
  override val type = value?.type ?: PlankType.unit

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitReturnStmt(this)
  }
}
