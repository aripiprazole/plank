package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
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

    fun visitStmts(many: List<ResolvedStmt>): List<T> = many.map(::visitStmt)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class ResolvedExprStmt(val expr: TypedExpr, override val location: Location) :
  ResolvedStmt,
  TypedPlankElement {
  override val ty: Ty = expr.ty
  override val subst: Subst = expr.subst

  override fun ap(subst: Subst): ResolvedExprStmt = copy(expr = expr.ap(subst))

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitExprStmt(this)
  }
}

data class ResolvedReturnStmt(val value: TypedExpr?, override val location: Location) :
  ResolvedStmt,
  TypedPlankElement {
  override val ty: Ty = value?.ty ?: unitTy
  override val subst: Subst = value?.subst ?: Subst()

  override fun ap(subst: Subst): ResolvedReturnStmt = copy(value = value?.ap(subst))

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitReturnStmt(this)
  }
}
