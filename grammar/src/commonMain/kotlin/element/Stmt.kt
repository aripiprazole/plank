package com.gabrielleeg1.plank.grammar.element

sealed interface Stmt : PlankElement {
  interface Visitor<T> {
    fun visit(stmt: Stmt): T = when (stmt) {
      is ExprStmt -> visitExprStmt(stmt)
      is ReturnStmt -> visitReturnStmt(stmt)
      is ErrorStmt -> visitErrorStmt(stmt)
      is EnumDecl -> visitEnumDecl(stmt)
      is StructDecl -> visitStructDecl(stmt)
      is ImportDecl -> visitImportDecl(stmt)
      is ModuleDecl -> visitModuleDecl(stmt)
      is FunDecl -> visitFunDecl(stmt)
      is LetDecl -> visitLetDecl(stmt)
      is ErrorDecl -> visitErrorDecl(stmt)
    }

    fun visitExprStmt(stmt: ExprStmt): T
    fun visitReturnStmt(stmt: ReturnStmt): T
    fun visitErrorStmt(stmt: ErrorStmt): T

    fun visitImportDecl(decl: ImportDecl): T
    fun visitModuleDecl(decl: ModuleDecl): T
    fun visitEnumDecl(decl: EnumDecl): T
    fun visitStructDecl(decl: StructDecl): T
    fun visitFunDecl(decl: FunDecl): T
    fun visitLetDecl(decl: LetDecl): T
    fun visitErrorDecl(decl: ErrorDecl): T

    fun visitStmts(many: List<Stmt>): List<T> = many.map(::visit)
  }
}

data class ExprStmt(val expr: Expr, override val location: Location) : Stmt

data class ReturnStmt(val value: Expr?, override val location: Location) : Stmt

data class ErrorStmt(
  override val message: String,
  override val arguments: List<Any>
) : Stmt, ErrorPlankElement {
  override val location = Location.Generated
}
