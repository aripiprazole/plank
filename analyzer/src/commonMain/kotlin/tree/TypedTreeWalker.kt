package com.lorenzoog.plank.analyzer.tree

import com.lorenzoog.plank.analyzer.element.TypedDecl
import com.lorenzoog.plank.analyzer.element.TypedExpr
import com.lorenzoog.plank.analyzer.element.TypedPlankElement
import com.lorenzoog.plank.analyzer.element.TypedPlankFile
import com.lorenzoog.plank.analyzer.element.TypedStmt
import com.lorenzoog.plank.analyzer.element.visit

abstract class TypedTreeWalker :
  TypedExpr.Visitor<Unit>,
  TypedStmt.Visitor<Unit>,
  TypedPlankFile.Visitor<Unit> {
  fun walk(element: TypedPlankElement) = when (element) {
    is TypedExpr -> visit(element)
    is TypedStmt -> visit(element)
    is TypedPlankFile -> visit(element)
    else -> error("Could not visit ${element::class}")
  }

  override fun visitPlankFile(file: TypedPlankFile) {
    visit(file.program)
  }

  override fun visitIfExpr(anIf: TypedExpr.If) {
    visit(anIf.cond)
    visit(anIf.thenBranch)
    visit(anIf.elseBranch)
  }

  override fun visitConstExpr(const: TypedExpr.Const) {
  }

  override fun visitAccessExpr(access: TypedExpr.Access) {
  }

  override fun visitLogicalExpr(logical: TypedExpr.Logical) {
    visit(logical.rhs)
    visit(logical.lhs)
  }

  override fun visitBinaryExpr(binary: TypedExpr.Binary) {
    visit(binary.rhs)
    visit(binary.lhs)
  }

  override fun visitUnaryExpr(unary: TypedExpr.Unary) {
    visit(unary.rhs)
  }

  override fun visitCallExpr(call: TypedExpr.Call) {
    visit(call.callee)
    visit(call.arguments)
  }

  override fun visitAssignExpr(assign: TypedExpr.Assign) {
    visit(assign.value)
  }

  override fun visitSetExpr(set: TypedExpr.Set) {
    visit(set.receiver)
    visit(set.value)
  }

  override fun visitGetExpr(get: TypedExpr.Get) {
    visit(get.receiver)
  }

  override fun visitGroupExpr(group: TypedExpr.Group) {
    visit(group.expr)
  }

  override fun visitInstanceExpr(instance: TypedExpr.Instance) {
    visit(instance.arguments.values.toList())
  }

  override fun visitSizeofExpr(sizeof: TypedExpr.Sizeof) {
  }

  override fun visitReferenceExpr(reference: TypedExpr.Reference) {
    visit(reference.expr)
  }

  override fun visitValueExpr(value: TypedExpr.Value) {
    visit(value.expr)
  }

  override fun visitConcatExpr(concat: TypedExpr.Concat) {
    visit(concat.lhs)
    visit(concat.rhs)
  }

  override fun visitImportDecl(importDecl: TypedDecl.ImportDecl) {
  }

  override fun visitExprStmt(exprStmt: TypedStmt.ExprStmt) {
    visit(exprStmt.expr)
  }

  override fun visitReturnStmt(returnStmt: TypedStmt.ReturnStmt) {
    visit(returnStmt.value ?: return)
  }

  override fun visitModuleDecl(moduleDecl: TypedDecl.ModuleDecl) {
    visit(moduleDecl.content)
  }

  override fun visitStructDecl(structDecl: TypedDecl.StructDecl) {
  }

  override fun visitFunDecl(funDecl: TypedDecl.FunDecl) {
    visit(funDecl.body)
  }

  override fun visitLetDecl(letDecl: TypedDecl.LetDecl) {
    visit(letDecl.value)
  }

  override fun visitEnumDecl(enumDecl: TypedDecl.EnumDecl) {
  }

  override fun visitMatchExpr(match: TypedExpr.Match) {
    visit(match.subject)
    visit(match.patterns.values.toList())
  }
}
