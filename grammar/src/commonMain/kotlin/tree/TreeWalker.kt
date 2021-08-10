package com.lorenzoog.plank.grammar.tree

import com.lorenzoog.plank.grammar.element.AccessExpr
import com.lorenzoog.plank.grammar.element.AccessTypeRef
import com.lorenzoog.plank.grammar.element.ArrayTypeRef
import com.lorenzoog.plank.grammar.element.AssignExpr
import com.lorenzoog.plank.grammar.element.CallExpr
import com.lorenzoog.plank.grammar.element.ConstExpr
import com.lorenzoog.plank.grammar.element.DerefExpr
import com.lorenzoog.plank.grammar.element.EnumDecl
import com.lorenzoog.plank.grammar.element.ErrorDecl
import com.lorenzoog.plank.grammar.element.ErrorExpr
import com.lorenzoog.plank.grammar.element.ErrorStmt
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.ExprStmt
import com.lorenzoog.plank.grammar.element.FunDecl
import com.lorenzoog.plank.grammar.element.FunctionTypeRef
import com.lorenzoog.plank.grammar.element.GetExpr
import com.lorenzoog.plank.grammar.element.GroupExpr
import com.lorenzoog.plank.grammar.element.IdentPattern
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.IfExpr
import com.lorenzoog.plank.grammar.element.ImportDecl
import com.lorenzoog.plank.grammar.element.InstanceExpr
import com.lorenzoog.plank.grammar.element.LetDecl
import com.lorenzoog.plank.grammar.element.MatchExpr
import com.lorenzoog.plank.grammar.element.ModuleDecl
import com.lorenzoog.plank.grammar.element.NamedTuplePattern
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.PointerTypeRef
import com.lorenzoog.plank.grammar.element.QualifiedPath
import com.lorenzoog.plank.grammar.element.QualifiedPathCons
import com.lorenzoog.plank.grammar.element.QualifiedPathNil
import com.lorenzoog.plank.grammar.element.RefExpr
import com.lorenzoog.plank.grammar.element.ReturnStmt
import com.lorenzoog.plank.grammar.element.SetExpr
import com.lorenzoog.plank.grammar.element.SizeofExpr
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.StructDecl
import com.lorenzoog.plank.grammar.element.TypeRef
import com.lorenzoog.plank.grammar.element.visit

class TreeWalker :
  Expr.Visitor<Unit>,
  Stmt.Visitor<Unit>,
  Pattern.Visitor<Unit>,
  QualifiedPath.Visitor<Unit>,
  Identifier.Visitor<Unit>,
  TypeRef.Visitor<Unit> {
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

  override fun visitMatchExpr(expr: MatchExpr) {
    visit(expr.subject)
    expr.patterns.forEach { (pattern, value) ->
      visit(pattern)
      visit(value)
    }
  }

  override fun visitIfExpr(expr: IfExpr) {
    visit(expr.cond)
    visit(expr.thenBranch)
    expr.elseBranch?.let {
      visit(it)
    }
  }

  override fun visitConstExpr(expr: ConstExpr) {
  }

  override fun visitAccessExpr(expr: AccessExpr) {
    visit(expr.path)
  }

  override fun visitCallExpr(expr: CallExpr) {
    visit(expr.callee)
    visit(expr.arguments)
  }

  override fun visitAssignExpr(expr: AssignExpr) {
    visit(expr.name)
    visit(expr.value)
  }

  override fun visitSetExpr(expr: SetExpr) {
    visit(expr.receiver)
    visit(expr.property)
    visit(expr.value)
  }

  override fun visitGetExpr(expr: GetExpr) {
    visit(expr.receiver)
    visit(expr.property)
  }

  override fun visitGroupExpr(expr: GroupExpr) {
    visit(expr.expr)
  }

  override fun visitInstanceExpr(expr: InstanceExpr) {
    visit(expr.type)

    expr.arguments.forEach { (property, value) ->
      visit(property)
      visit(value)
    }
  }

  override fun visitSizeofExpr(expr: SizeofExpr) {
    visit(expr.type)
  }

  override fun visitRefExpr(expr: RefExpr) {
    visit(expr.expr)
  }

  override fun visitDerefExpr(expr: DerefExpr) {
    visit(expr.expr)
  }

  override fun visitErrorExpr(expr: ErrorExpr) {
  }

  override fun visitExprStmt(stmt: ExprStmt) {
    visit(stmt.expr)
  }

  override fun visitReturnStmt(stmt: ReturnStmt) {
    stmt.value?.let { visit(it) }
  }

  override fun visitErrorStmt(stmt: ErrorStmt) {
  }

  override fun visitImportDecl(decl: ImportDecl) {
    visit(decl.path)
  }

  override fun visitModuleDecl(decl: ModuleDecl) {
    visit(decl.path)
    visit(decl.content)
  }

  override fun visitEnumDecl(decl: EnumDecl) {
    visit(decl.name)

    decl.members.forEach { (member, parameters) ->
      visit(member)
      visit(parameters)
    }
  }

  override fun visitStructDecl(decl: StructDecl) {
    visit(decl.name)
    decl.properties.forEach { (_, property, type) ->
      visit(property)
      visit(type)
    }
  }

  override fun visitFunDecl(decl: FunDecl) {
    visit(decl.name)
    visit(decl.type)
    visit(decl.body)
    decl.realParameters.forEach { (parameter, type) ->
      visit(parameter)
      visit(type)
    }
  }

  override fun visitLetDecl(decl: LetDecl) {
    visit(decl.name)
    decl.type?.let { visit(it) }
    visit(decl.value)
  }

  override fun visitErrorDecl(decl: ErrorDecl) {
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef) {
    visit(ref.path)
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef) {
    visit(ref.type)
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef) {
    visit(ref.type)
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef) {
    visit(ref.parameters)
    visit(ref.returnType)
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern) {
    visit(pattern.type)
    visit(pattern.fields)
  }

  override fun visitIdentPattern(pattern: IdentPattern) {
    visit(pattern.name)
  }

  override fun visitQualifiedPathCons(path: QualifiedPathCons) {
    visit(path.value)
    visit(path.next)
  }

  override fun visitQualifiedPathNil(path: QualifiedPathNil) {
  }

  override fun visitIdentifier(identifier: Identifier) {
  }
}
