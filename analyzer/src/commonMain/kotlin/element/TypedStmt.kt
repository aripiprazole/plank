package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.Location

abstract class TypedStmt internal constructor() : TypedPlankElement {
  interface Visitor<T> {
    fun visit(stmt: TypedStmt): T = stmt.accept(this)

    fun visitExprStmt(exprStmt: ExprStmt): T
    fun visitReturnStmt(returnStmt: ReturnStmt): T

    fun visitImportDecl(importDecl: TypedDecl.ImportDecl): T
    fun visitModuleDecl(moduleDecl: TypedDecl.ModuleDecl): T
    fun visitEnumDecl(enumDecl: TypedDecl.EnumDecl): T
    fun visitStructDecl(structDecl: TypedDecl.StructDecl): T
    fun visitFunDecl(funDecl: TypedDecl.FunDecl): T
    fun visitLetDecl(letDecl: TypedDecl.LetDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class ExprStmt(
    val expr: TypedExpr,
    override val type: PlankType,
    override val location: Location,
  ) : TypedStmt() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitExprStmt(this)
    }
  }

  data class ReturnStmt(
    val value: TypedExpr?,
    override val type: PlankType,
    override val location: Location,
  ) : TypedStmt() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitReturnStmt(this)
    }
  }
}
