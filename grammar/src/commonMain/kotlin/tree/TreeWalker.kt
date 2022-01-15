package com.gabrielleeg1.plank.grammar.tree

import com.gabrielleeg1.plank.grammar.element.AccessExpr
import com.gabrielleeg1.plank.grammar.element.AccessTypeRef
import com.gabrielleeg1.plank.grammar.element.ArrayTypeRef
import com.gabrielleeg1.plank.grammar.element.AssignExpr
import com.gabrielleeg1.plank.grammar.element.CallExpr
import com.gabrielleeg1.plank.grammar.element.ConstExpr
import com.gabrielleeg1.plank.grammar.element.DerefExpr
import com.gabrielleeg1.plank.grammar.element.EnumDecl
import com.gabrielleeg1.plank.grammar.element.ErrorDecl
import com.gabrielleeg1.plank.grammar.element.ErrorExpr
import com.gabrielleeg1.plank.grammar.element.ErrorStmt
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.ExprStmt
import com.gabrielleeg1.plank.grammar.element.FunDecl
import com.gabrielleeg1.plank.grammar.element.FunctionTypeRef
import com.gabrielleeg1.plank.grammar.element.GetExpr
import com.gabrielleeg1.plank.grammar.element.GroupExpr
import com.gabrielleeg1.plank.grammar.element.IdentPattern
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.IfExpr
import com.gabrielleeg1.plank.grammar.element.ImportDecl
import com.gabrielleeg1.plank.grammar.element.InstanceExpr
import com.gabrielleeg1.plank.grammar.element.LetDecl
import com.gabrielleeg1.plank.grammar.element.MatchExpr
import com.gabrielleeg1.plank.grammar.element.ModuleDecl
import com.gabrielleeg1.plank.grammar.element.NamedTuplePattern
import com.gabrielleeg1.plank.grammar.element.Pattern
import com.gabrielleeg1.plank.grammar.element.PlankElement
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.element.PointerTypeRef
import com.gabrielleeg1.plank.grammar.element.QualifiedPath
import com.gabrielleeg1.plank.grammar.element.RefExpr
import com.gabrielleeg1.plank.grammar.element.ReturnStmt
import com.gabrielleeg1.plank.grammar.element.SetExpr
import com.gabrielleeg1.plank.grammar.element.SizeofExpr
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.grammar.element.StructDecl
import com.gabrielleeg1.plank.grammar.element.TypeRef
import com.gabrielleeg1.plank.grammar.element.visit

abstract class TreeWalker :
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
    visit(expr.value)
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
    visit(expr.ref)
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

  override fun visitQualifiedPath(path: QualifiedPath) {
    path.fullPath.forEach {
      visit(it)
    }
  }

  override fun visitIdentifier(identifier: Identifier) {
  }
}
