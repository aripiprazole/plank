package com.gabrielleeg1.plank.grammar.element

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
    visitStmts(file.program)
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
    visitExprs(expr.arguments)
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
    visitStmts(decl.content)
  }

  override fun visitEnumDecl(decl: EnumDecl) {
    visit(decl.name)

    decl.members.forEach { (member, parameters) ->
      visit(member)
      visitTypeRefs(parameters)
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
    visitStmts(decl.body)
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
    visit(ref.parameter)
    visit(ref.returnType)
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef) {
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern) {
    visit(pattern.type)
    visitPatterns(pattern.fields)
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
