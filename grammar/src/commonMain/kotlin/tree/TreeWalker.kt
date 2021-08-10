package com.lorenzoog.plank.grammar.tree

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeRef
import com.lorenzoog.plank.grammar.element.visit

abstract class TreeWalker : Expr.Visitor<Unit>, Stmt.Visitor<Unit>, TypeRef.Visitor<Unit> {
  fun walk(element: PlankElement) = when (element) {
    is Expr -> visit(element)
    is Stmt -> visit(element)
    is TypeRef -> visit(element)
    is PlankFile -> walk(element)
    else -> error("Could not visit ${element::class}")
  }

  private fun walk(file: PlankFile) {
    visit(file.program)
  }

  override fun visitIfExpr(anIf: Expr.If) {
    visit(anIf.cond)
    visit(anIf.thenBranch)
    visit(anIf.elseBranch)
  }

  override fun visitConstExpr(const: Expr.Const) {
  }

  override fun visitAccessExpr(access: Expr.Access) {
  }

  override fun visitCallExpr(call: Expr.Call) {
    visit(call.callee)
    visit(call.arguments)
  }

  override fun visitAssignExpr(assign: Expr.Assign) {
    visit(assign.value)
  }

  override fun visitSetExpr(set: Expr.Set) {
    visit(set.receiver)
    visit(set.value)
  }

  override fun visitGetExpr(get: Expr.Get) {
    visit(get.receiver)
  }

  override fun visitGroupExpr(group: Expr.Group) {
    visit(group.expr)
  }

  override fun visitInstanceExpr(instance: Expr.Instance) {
    visit(instance.arguments.values.toList())
  }

  override fun visitSizeofExpr(sizeof: Expr.Sizeof) {
  }

  override fun visitRefExpr(reference: Expr.Reference) {
    visit(reference.expr)
  }

  override fun visitDerefExpr(value: Expr.Value) {
    visit(value.expr)
  }

  override fun visitImportDecl(importDecl: Decl.ImportDecl) {
  }

  override fun visitExprStmt(exprStmt: Stmt.ExprStmt) {
    visit(exprStmt.expr)
  }

  override fun visitReturnStmt(returnStmt: Stmt.ReturnStmt) {
    visit(returnStmt.value ?: return)
  }

  override fun visitModuleDecl(moduleDecl: Decl.ModuleDecl) {
    visit(moduleDecl.content)
  }

  override fun visitStructDecl(structDecl: Decl.StructDecl) {
    visit(structDecl.properties.map(Decl.StructDecl.Property::type))
  }

  override fun visitFunDecl(funDecl: Decl.FunDecl) {
    visit(funDecl.type)
    visit(funDecl.parameters)
    visit(funDecl.body)
  }

  override fun visitLetDecl(letDecl: Decl.LetDecl) {
    letDecl.type?.let(this::visit)
    visit(letDecl.value)
  }

  override fun visitEnumDecl(enumDecl: Decl.EnumDecl) {
    enumDecl.members.forEach {
      visit(it.fields)
    }
  }

  override fun visitMatchExpr(match: Expr.Match) {
    visit(match.subject)
    visit(match.patterns.values.toList())
  }

  override fun visitAccessTypeRef(ref: TypeRef.Access) {
  }

  override fun visitPointerTypeRef(ref: TypeRef.Pointer) {
    visit(ref.type)
  }

  override fun visitArrayTypeRef(ref: TypeRef.Array) {
    visit(ref.type)
  }

  override fun visitFunctionTypeRef(ref: TypeRef.Function) {
    visit(ref.parameters)
    ref.returnType?.let {
      visit(ref.returnType)
    }
  }
}
