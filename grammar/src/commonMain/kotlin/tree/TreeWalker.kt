@file:Suppress("MemberVisibilityCanBePrivate", "unused")

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

abstract class TreeWalker {
  open fun walk(element: PlankElement) = when (element) {
    is Expr -> walk(element)
    is Stmt -> walk(element)
    is TypeRef -> walk(element)
    is PlankFile -> walk(element)
    else -> error("Could not walk through ${element::class}")
  }

  open fun walk(typeRef: TypeRef) {
    when (typeRef) {
      is AccessTypeRef -> visitAccessTypeRef(typeRef)
      is PointerTypeRef -> visitPointerTypeRef(typeRef)
      is ArrayTypeRef -> visitArrayTypeRef(typeRef)
      is FunctionTypeRef -> visitFunctionTypeRef(typeRef)
    }
  }

  open fun walk(stmt: Stmt) {
    when (stmt) {
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
  }

  open fun walk(expr: Expr) {
    when (expr) {
      is MatchExpr -> visitMatchExpr(expr)
      is IfExpr -> visitIfExpr(expr)
      is ConstExpr -> visitConstExpr(expr)
      is AccessExpr -> visitAccessExpr(expr)
      is GroupExpr -> visitGroupExpr(expr)
      is AssignExpr -> visitAssignExpr(expr)
      is SetExpr -> visitSetExpr(expr)
      is GetExpr -> visitGetExpr(expr)
      is CallExpr -> visitCallExpr(expr)
      is InstanceExpr -> visitInstanceExpr(expr)
      is SizeofExpr -> visitSizeofExpr(expr)
      is RefExpr -> visitRefExpr(expr)
      is DerefExpr -> visitDerefExpr(expr)
      is ErrorExpr -> visitErrorExpr(expr)
    }
  }

  open fun walk(file: PlankFile) {
    walk(file.program)
  }

  open fun visitMatchExpr(expr: MatchExpr) {
    walk(expr.subject)
    expr.patterns.forEach { (pattern, value) ->
      walk(pattern)
      walk(value)
    }
  }

  open fun visitIfExpr(expr: IfExpr) {
    walk(expr.cond)
    walk(expr.thenBranch)
    expr.elseBranch?.let {
      walk(it)
    }
  }

  open fun visitConstExpr(expr: ConstExpr) {
  }

  open fun visitAccessExpr(expr: AccessExpr) {
    walk(expr.path)
  }

  open fun visitCallExpr(expr: CallExpr) {
    walk(expr.callee)
    walk(expr.arguments)
  }

  open fun visitAssignExpr(expr: AssignExpr) {
    walk(expr.name)
    walk(expr.value)
  }

  open fun visitSetExpr(expr: SetExpr) {
    walk(expr.receiver)
    walk(expr.property)
    walk(expr.value)
  }

  open fun visitGetExpr(expr: GetExpr) {
    walk(expr.receiver)
    walk(expr.property)
  }

  open fun visitGroupExpr(expr: GroupExpr) {
    walk(expr.value)
  }

  open fun visitInstanceExpr(expr: InstanceExpr) {
    walk(expr.type)

    expr.arguments.forEach { (property, value) ->
      walk(property)
      walk(value)
    }
  }

  open fun visitSizeofExpr(expr: SizeofExpr) {
    walk(expr.type)
  }

  open fun visitRefExpr(expr: RefExpr) {
    walk(expr.expr)
  }

  open fun visitDerefExpr(expr: DerefExpr) {
    walk(expr.ref)
  }

  open fun visitErrorExpr(expr: ErrorExpr) {
  }

  open fun visitExprStmt(stmt: ExprStmt) {
    walk(stmt.expr)
  }

  open fun visitReturnStmt(stmt: ReturnStmt) {
    stmt.value?.let { walk(it) }
  }

  open fun visitErrorStmt(stmt: ErrorStmt) {
  }

  open fun visitImportDecl(decl: ImportDecl) {
    walk(decl.path)
  }

  open fun visitModuleDecl(decl: ModuleDecl) {
    walk(decl.path)
    walk(decl.content)
  }

  open fun visitEnumDecl(decl: EnumDecl) {
    walk(decl.name)

    decl.members.forEach { (member, parameters) ->
      walk(member)
      walk(parameters)
    }
  }

  open fun visitStructDecl(decl: StructDecl) {
    walk(decl.name)
    decl.properties.forEach { (_, property, type) ->
      walk(property)
      walk(type)
    }
  }

  open fun visitFunDecl(decl: FunDecl) {
    walk(decl.name)
    walk(decl.type)
    walk(decl.body)
    decl.realParameters.forEach { (parameter, type) ->
      walk(parameter)
      walk(type)
    }
  }

  open fun visitLetDecl(decl: LetDecl) {
    walk(decl.name)
    decl.type?.let { walk(it) }
    walk(decl.value)
  }

  open fun visitErrorDecl(decl: ErrorDecl) {
  }

  open fun visitAccessTypeRef(ref: AccessTypeRef) {
    walk(ref.path)
  }

  open fun visitPointerTypeRef(ref: PointerTypeRef) {
    walk(ref.type)
  }

  open fun visitArrayTypeRef(ref: ArrayTypeRef) {
    walk(ref.type)
  }

  open fun visitFunctionTypeRef(ref: FunctionTypeRef) {
    walk(ref.parameters)
    walk(ref.returnType)
  }

  open fun visitNamedTuplePattern(pattern: NamedTuplePattern) {
    walk(pattern.type)
    walk(pattern.fields)
  }

  open fun visitIdentPattern(pattern: IdentPattern) {
    walk(pattern.name)
  }

  open fun visitQualifiedPath(path: QualifiedPath) {
    path.fullPath.forEach {
      walk(it)
    }
  }

  open fun visitIdentifier(identifier: Identifier) {
  }

  open fun walk(patterns: List<Pattern>) {
    patterns.map(::walk)
  }

  open fun walk(types: List<TypeRef>) {
    types.map(::walk)
  }

  open fun walk(stmts: List<Stmt>) {
    stmts.map(::walk)
  }

  open fun walk(exprs: List<Expr>) {
    exprs.map(::walk)
  }
}
