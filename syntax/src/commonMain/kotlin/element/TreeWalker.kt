package org.plank.syntax.element

@Suppress("EmptyFunctionBlock")
open class TreeWalker :
  Expr.Visitor<Unit>,
  Stmt.Visitor<Unit>,
  Pattern.Visitor<Unit>,
  QualifiedPath.Visitor<Unit>,
  Identifier.Visitor<Unit>,
  TypeRef.Visitor<Unit>,
  FunctionBody.Visitor<Unit>,
  IfBranch.Visitor<Unit> {
  fun walk(element: PlankElement) = when (element) {
    is Expr -> visitExpr(element)
    is Stmt -> visitStmt(element)
    is TypeRef -> visitTypeRef(element)
    is PlankFile -> walk(element)
    else -> error("Could not visit ${element::class}")
  }

  private fun walk(file: PlankFile) {
    visitStmts(file.program)
  }

  override fun visitMatchExpr(expr: MatchExpr) {
    visitExpr(expr.subject)
    expr.patterns.forEach { (pattern, value) ->
      visitPattern(pattern)
      visitExpr(value)
    }
  }

  override fun visitIfExpr(expr: IfExpr) {
    visitExpr(expr.cond)
    visitIfBranch(expr.thenBranch)
    expr.elseBranch?.let { visitIfBranch(it) }
  }

  override fun visitConstExpr(expr: ConstExpr) {
  }

  override fun visitAccessExpr(expr: AccessExpr) {
    visitQualifiedPath(expr.path)
  }

  override fun visitCallExpr(expr: CallExpr) {
    visitExpr(expr.callee)
    visitExprs(expr.arguments)
  }

  override fun visitAssignExpr(expr: AssignExpr) {
    visitIdentifier(expr.name)
    visitExpr(expr.value)
  }

  override fun visitSetExpr(expr: SetExpr) {
    visitExpr(expr.receiver)
    visitIdentifier(expr.property)
    visitExpr(expr.value)
  }

  override fun visitGetExpr(expr: GetExpr) {
    visitExpr(expr.receiver)
    visitIdentifier(expr.property)
  }

  override fun visitGroupExpr(expr: GroupExpr) {
    visitExpr(expr.value)
  }

  override fun visitInstanceExpr(expr: InstanceExpr) {
    visitTypeRef(expr.type)

    expr.arguments.forEach { (property, value) ->
      visitIdentifier(property)
      visitExpr(value)
    }
  }

  override fun visitSizeofExpr(expr: SizeofExpr) {
    visitTypeRef(expr.type)
  }

  override fun visitRefExpr(expr: RefExpr) {
    visitExpr(expr.value)
  }

  override fun visitDerefExpr(expr: DerefExpr) {
    visitExpr(expr.value)
  }

  override fun visitExprStmt(stmt: ExprStmt) {
    visitExpr(stmt.expr)
  }

  override fun visitReturnStmt(stmt: ReturnStmt) {
    stmt.value?.let { visitExpr(it) }
  }

  override fun visitUseDecl(decl: UseDecl) {
    visitQualifiedPath(decl.path)
  }

  override fun visitModuleDecl(decl: ModuleDecl) {
    visitQualifiedPath(decl.path)
    visitStmts(decl.content)
  }

  override fun visitEnumDecl(decl: EnumDecl) {
    visitIdentifier(decl.name)

    decl.members.forEach { (member, parameters) ->
      visitIdentifier(member)
      visitTypeRefs(parameters)
    }
  }

  override fun visitStructDecl(decl: StructDecl) {
    visitIdentifier(decl.name)
    decl.properties.forEach { (_, property, type) ->
      visitIdentifier(property)
      visitTypeRef(type)
    }
  }

  override fun visitFunDecl(decl: FunDecl) {
    visitIdentifier(decl.name)
    visitFunctionBody(decl.body)
    decl.parameters.forEach { (parameter, type) ->
      visitIdentifier(parameter)
      visitTypeRef(type)
    }
    visitTypeRef(decl.returnType)
  }

  override fun visitLetDecl(decl: LetDecl) {
    visitIdentifier(decl.name)
    decl.type?.let { visitTypeRef(it) }
    visitExpr(decl.value)
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef) {
    visitQualifiedPath(ref.path)
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef) {
    visitTypeRef(ref.type)
  }

  override fun visitApplyTypeRef(ref: ApplyTypeRef) {
    visitQualifiedPath(ref.function)
    visitTypeRefs(ref.arguments)
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef) {
    visitTypeRef(ref.parameterType)
    visitTypeRef(ref.returnType)
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef) {
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern) {
    visitQualifiedPath(pattern.type)
    visitPatterns(pattern.properties)
  }

  override fun visitIdentPattern(pattern: IdentPattern) {
    visitIdentifier(pattern.name)
  }

  override fun visitQualifiedPath(path: QualifiedPath) {
    path.fullPath.forEach {
      visitIdentifier(it)
    }
  }

  override fun visitIdentifier(identifier: Identifier) {
  }

  override fun visitNoBody(body: NoBody) {
  }

  override fun visitExprBody(body: ExprBody) {
    visitExpr(body.expr)
  }

  override fun visitCodeBody(body: CodeBody) {
    visitStmts(body.stmts)
    body.value?.let { visitExpr(it) }
  }

  override fun visitBlockExpr(expr: BlockExpr) {
    visitStmts(expr.stmts)
    expr.value?.let { visitExpr(it) }
  }

  override fun visitThenBranch(branch: ThenBranch) {
    visitExpr(branch.value)
  }

  override fun visitBlockBranch(branch: BlockBranch) {
    visitStmts(branch.stmts)
    branch.value?.let { visitExpr(it) }
  }
}
