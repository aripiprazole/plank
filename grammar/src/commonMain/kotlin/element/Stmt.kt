package com.lorenzoog.plank.grammar.element

abstract class Stmt internal constructor() : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: Stmt): T = stmt.accept(this)

    fun visitExprStmt(exprStmt: ExprStmt): T
    fun visitReturnStmt(returnStmt: ReturnStmt): T

    fun visitImportDecl(importDecl: Decl.ImportDecl): T
    fun visitModuleDecl(moduleDecl: Decl.ModuleDecl): T
    fun visitClassDecl(structDecl: Decl.StructDecl): T
    fun visitFunDecl(funDecl: Decl.FunDecl): T
    fun visitLetDecl(letDecl: Decl.LetDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class ExprStmt(
    val expr: Expr,
    override val location: Location
  ) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitExprStmt(this)
    }
  }

  data class ReturnStmt(
    val value: Expr?,
    override val location: Location
  ) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitReturnStmt(this)
    }
  }
}
