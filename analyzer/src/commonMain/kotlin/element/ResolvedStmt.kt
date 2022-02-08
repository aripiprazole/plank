package org.plank.analyzer.element

import org.plank.analyzer.UnitType
import org.plank.syntax.element.ErrorPlankElement
import org.plank.syntax.element.Location

sealed interface ResolvedStmt : ResolvedPlankElement {
  interface Visitor<T> {
    fun visitStmt(stmt: ResolvedStmt): T = stmt.accept(this)

    fun visitExprStmt(stmt: ResolvedExprStmt): T
    fun visitReturnStmt(stmt: ResolvedReturnStmt): T

    fun visitUseDecl(decl: ResolvedUseDecl): T
    fun visitModuleDecl(decl: ResolvedModuleDecl): T
    fun visitEnumDecl(decl: ResolvedEnumDecl): T
    fun visitStructDecl(decl: ResolvedStructDecl): T
    fun visitFunDecl(decl: ResolvedFunDecl): T
    fun visitLetDecl(decl: ResolvedLetDecl): T

    fun visitViolatedStmt(stmt: ResolvedErrorStmt): T
    fun visitViolatedDecl(stmt: ResolvedErrorDecl): T

    fun visitStmts(many: List<ResolvedStmt>): List<T> = many.map(::visitStmt)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ResolvedExprStmt(val expr: TypedExpr, override val location: Location) :
  ResolvedStmt,
  TypedPlankElement {
  override val type = expr.type

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitExprStmt(this)
  }
}

data class ResolvedReturnStmt(val value: TypedExpr?, override val location: Location) :
  ResolvedStmt,
  TypedPlankElement {
  override val type = value?.type ?: UnitType

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitReturnStmt(this)
  }
}

data class ResolvedErrorStmt(
  override val message: String,
  override val arguments: List<Any>,
) : ResolvedStmt, ErrorPlankElement {
  override val location = Location.Generated

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitViolatedStmt(this)
  }
}
