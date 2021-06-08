package com.lorenzoog.plank.grammar.element

abstract class Stmt internal constructor() : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: Stmt): T = stmt.accept(this)

    fun visitExprStmt(exprStmt: ExprStmt): T
    fun visitReturnStmt(returnStmt: ReturnStmt): T

    fun visitImportDecl(decl: ImportDecl): T
    fun visitModuleDecl(decl: ModuleDecl): T
    fun visitEnumDecl(decl: EnumDecl): T
    fun visitStructDecl(decl: StructDecl): T
    fun visitFunDecl(decl: FunDecl): T
    fun visitLetDecl(decl: LetDecl): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
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
