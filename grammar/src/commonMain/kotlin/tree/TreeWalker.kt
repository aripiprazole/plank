package com.lorenzoog.plank.grammar.tree

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeDef
import com.lorenzoog.plank.grammar.element.visit

abstract class TreeWalker : Expr.Visitor<Unit>, Stmt.Visitor<Unit>, TypeDef.Visitor<Unit> {
  fun walk(element: PlankElement) = when (element) {
    is Expr -> visit(element)
    is Stmt -> visit(element)
    is TypeDef -> visit(element)
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

  override fun visitLogicalExpr(logical: Expr.Logical) {
    visit(logical.rhs)
    visit(logical.lhs)
  }

  override fun visitBinaryExpr(binary: Expr.Binary) {
    visit(binary.rhs)
    visit(binary.lhs)
  }

  override fun visitUnaryExpr(unary: Expr.Unary) {
    visit(unary.rhs)
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

  override fun visitReferenceExpr(reference: Expr.Reference) {
    visit(reference.expr)
  }

  override fun visitValueExpr(value: Expr.Value) {
    visit(value.expr)
  }

  override fun visitConcatExpr(concat: Expr.Concat) {
    visit(concat.lhs)
    visit(concat.rhs)
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

  override fun visitClassDecl(structDecl: Decl.StructDecl) {
    visit(structDecl.fields.map(Decl.StructDecl.Field::type))
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

  override fun visitGenericAccess(access: TypeDef.GenericAccess) {
  }

  override fun visitGenericUse(use: TypeDef.GenericUse) {
    visit(use.arguments)
    visit(use.receiver)
  }

  override fun visitNameTypeDef(name: TypeDef.Name) {
  }

  override fun visitPtrTypeDef(ptr: TypeDef.Ptr) {
    visit(ptr.type)
  }

  override fun visitArrayTypeDef(array: TypeDef.Array) {
    visit(array.type)
  }

  override fun visitFunctionTypeDef(function: TypeDef.Function) {
    function.returnType?.let(this::visit)
    visit(function.parameters)
  }
}
