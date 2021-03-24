package com.lorenzoog.jplank.element

import org.antlr.v4.kotlinruntime.Token

abstract class Stmt internal constructor() : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: Stmt): T = stmt.accept(this)

    fun visitImportStmt(importStmt: ImportStmt): T
    fun visitExprStmt(exprStmt: ExprStmt): T
    fun visitReturnStmt(returnStmt: ReturnStmt): T

    fun visitModuleDecl(moduleDecl: Decl.ModuleDecl): T
    fun visitClassDecl(structDecl: Decl.StructDecl): T
    fun visitFunDecl(funDecl: Decl.FunDecl): T
    fun visitLetDecl(letDecl: Decl.LetDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class ImportStmt(val module: Token, override val location: Location) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitImportStmt(this)
    }
  }

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
