package org.plank.syntax.element

sealed interface Stmt : PlankElement {
  interface Visitor<T> {
    fun visitStmt(stmt: Stmt): T = stmt.accept(this)

    fun visitExprStmt(stmt: ExprStmt): T
    fun visitReturnStmt(stmt: ReturnStmt): T
    fun visitErrorStmt(stmt: ErrorStmt): T = error("found error stmt")

    fun visitUseDecl(decl: UseDecl): T
    fun visitModuleDecl(decl: ModuleDecl): T
    fun visitEnumDecl(decl: EnumDecl): T
    fun visitStructDecl(decl: StructDecl): T
    fun visitFunDecl(decl: FunDecl): T
    fun visitLetDecl(decl: LetDecl): T
    fun visitErrorDecl(decl: ErrorDecl): T = error("found error decl")

    fun visitStmts(many: List<Stmt>): List<T> = many.map(::visitStmt)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ExprStmt(val expr: Expr, override val location: Location) : Stmt {
  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitExprStmt(this)
  }
}

data class ReturnStmt(val value: Expr?, override val location: Location) : Stmt {
  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitReturnStmt(this)
  }
}

data class ErrorStmt(
  override val message: String,
  override val arguments: List<Any>
) : Stmt, ErrorPlankElement {
  override val location = Location.Generated

  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitErrorStmt(this)
  }
}
