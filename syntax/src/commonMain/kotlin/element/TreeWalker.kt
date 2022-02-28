package org.plank.syntax.element

@Suppress("EmptyFunctionBlock", "TooManyFunctions")
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

  open fun walkBlockExpr(expr: BlockExpr) {
  }

  open fun walkMatchExpr(expr: MatchExpr) {
  }

  open fun walkIfExpr(expr: IfExpr) {
  }

  open fun walkConstExpr(expr: ConstExpr) {
  }

  open fun walkAccessExpr(expr: AccessExpr) {
  }

  open fun walkCallExpr(expr: CallExpr) {
  }

  open fun walkAssignExpr(expr: AssignExpr) {
  }

  open fun walkSetExpr(expr: SetExpr) {
  }

  open fun walkGetExpr(expr: GetExpr) {
  }

  open fun walkGroupExpr(expr: GroupExpr) {
  }

  open fun walkInstanceExpr(expr: InstanceExpr) {
  }

  open fun walkSizeofExpr(expr: SizeofExpr) {
  }

  open fun walkRefExpr(expr: RefExpr) {
  }

  open fun walkDerefExpr(expr: DerefExpr) {
  }

  open fun walkNoBody(body: NoBody) {
  }

  open fun walkExprBody(body: ExprBody) {
  }

  open fun walkCodeBody(body: CodeBody) {
  }

  open fun walkIdentifier(identifier: Identifier) {
  }

  open fun walkThenBranch(branch: ThenBranch) {
  }

  open fun walkBlockBranch(branch: BlockBranch) {
  }

  open fun walkNamedTuplePattern(pattern: EnumVariantPattern) {
  }

  open fun walkIdentPattern(pattern: IdentPattern) {
  }

  open fun walkQualifiedPath(path: QualifiedPath) {
  }

  open fun walkExprStmt(stmt: ExprStmt) {
  }

  open fun walkReturnStmt(stmt: ReturnStmt) {
  }

  open fun walkUseDecl(decl: UseDecl) {
  }

  open fun walkModuleDecl(decl: ModuleDecl) {
  }

  open fun walkEnumDecl(decl: EnumDecl) {
  }

  open fun walkStructDecl(decl: StructDecl) {
  }

  open fun walkFunDecl(decl: FunDecl) {
  }

  open fun walkLetDecl(decl: LetDecl) {
  }

  open fun walkAccessTypeRef(ref: AccessTypeRef) {
  }

  open fun walkGenericTypeRef(ref: GenericTypeRef) {
  }

  open fun walkPointerTypeRef(ref: PointerTypeRef) {
  }

  open fun walkApplyTypeRef(ref: ApplyTypeRef) {
  }

  open fun walkFunctionTypeRef(ref: FunctionTypeRef) {
  }

  open fun walkUnitTypeRef(ref: UnitTypeRef) {
  }

  override fun visitMatchExpr(expr: MatchExpr) {
    visitExpr(expr.subject)
    expr.patterns.forEach { (pattern, value) ->
      visitPattern(pattern)
      visitExpr(value)
    }
    walkMatchExpr(expr)
  }

  override fun visitIfExpr(expr: IfExpr) {
    visitExpr(expr.cond)
    visitIfBranch(expr.thenBranch)
    expr.elseBranch?.let { visitIfBranch(it) }
    walkIfExpr(expr)
  }

  override fun visitConstExpr(expr: ConstExpr) {
    walkConstExpr(expr)
  }

  override fun visitAccessExpr(expr: AccessExpr) {
    visitIdentifier(expr.name)
    walkAccessExpr(expr)
  }

  override fun visitCallExpr(expr: CallExpr) {
    visitExpr(expr.callee)
    visitExprs(expr.arguments)
    walkCallExpr(expr)
  }

  override fun visitAssignExpr(expr: AssignExpr) {
    visitIdentifier(expr.name)
    visitExpr(expr.value)
    walkAssignExpr(expr)
  }

  override fun visitSetExpr(expr: SetExpr) {
    visitExpr(expr.receiver)
    visitIdentifier(expr.property)
    visitExpr(expr.value)
    walkSetExpr(expr)
  }

  override fun visitGetExpr(expr: GetExpr) {
    visitExpr(expr.receiver)
    visitIdentifier(expr.property)
    walkGetExpr(expr)
  }

  override fun visitGroupExpr(expr: GroupExpr) {
    visitExpr(expr.value)
    walkGroupExpr(expr)
  }

  override fun visitInstanceExpr(expr: InstanceExpr) {
    visitTypeRef(expr.type)

    expr.arguments.forEach { (property, value) ->
      visitIdentifier(property)
      visitExpr(value)
    }

    walkInstanceExpr(expr)
  }

  override fun visitSizeofExpr(expr: SizeofExpr) {
    visitTypeRef(expr.type)
    walkSizeofExpr(expr)
  }

  override fun visitRefExpr(expr: RefExpr) {
    visitExpr(expr.value)
    walkRefExpr(expr)
  }

  override fun visitDerefExpr(expr: DerefExpr) {
    visitExpr(expr.value)
    walkDerefExpr(expr)
  }

  override fun visitExprStmt(stmt: ExprStmt) {
    visitExpr(stmt.expr)
    walkExprStmt(stmt)
  }

  override fun visitReturnStmt(stmt: ReturnStmt) {
    stmt.value?.let { visitExpr(it) }
    walkReturnStmt(stmt)
  }

  override fun visitUseDecl(decl: UseDecl) {
    visitQualifiedPath(decl.path)
    walkUseDecl(decl)
  }

  override fun visitModuleDecl(decl: ModuleDecl) {
    visitQualifiedPath(decl.path)
    visitStmts(decl.content)
    walkModuleDecl(decl)
  }

  override fun visitEnumDecl(decl: EnumDecl) {
    visitIdentifier(decl.name)

    decl.members.forEach { (member, parameters) ->
      visitIdentifier(member)
      visitTypeRefs(parameters)
    }

    walkEnumDecl(decl)
  }

  override fun visitStructDecl(decl: StructDecl) {
    visitIdentifier(decl.name)
    decl.properties.forEach { (_, property, type) ->
      visitIdentifier(property)
      visitTypeRef(type)
    }
    walkStructDecl(decl)
  }

  override fun visitFunDecl(decl: FunDecl) {
    visitIdentifier(decl.name)
    visitFunctionBody(decl.body)
    decl.parameters.forEach { (parameter, type) ->
      visitIdentifier(parameter)
      visitTypeRef(type)
    }
    visitTypeRef(decl.returnType)
    walkFunDecl(decl)
  }

  override fun visitLetDecl(decl: LetDecl) {
    visitIdentifier(decl.name)
    decl.type?.let { visitTypeRef(it) }
    visitExpr(decl.value)
    walkLetDecl(decl)
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef) {
    visitQualifiedPath(ref.path)
    walkAccessTypeRef(ref)
  }

  override fun visitGenericTypeRef(ref: GenericTypeRef) {
    visitIdentifier(ref.name)
    walkGenericTypeRef(ref)
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef) {
    visitTypeRef(ref.type)
    walkPointerTypeRef(ref)
  }

  override fun visitApplyTypeRef(ref: ApplyTypeRef) {
    visitTypeRef(ref.function)
    visitTypeRefs(ref.arguments)
    walkApplyTypeRef(ref)
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef) {
    visitTypeRef(ref.parameterType)
    visitTypeRef(ref.returnType)
    walkFunctionTypeRef(ref)
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef) {
    walkUnitTypeRef(ref)
  }

  override fun visitNamedTuplePattern(pattern: EnumVariantPattern) {
    visitQualifiedPath(pattern.type)
    visitPatterns(pattern.properties)
    walkNamedTuplePattern(pattern)
  }

  override fun visitIdentPattern(pattern: IdentPattern) {
    visitIdentifier(pattern.name)
    walkIdentPattern(pattern)
  }

  override fun visitQualifiedPath(path: QualifiedPath) {
    path.fullPath.forEach {
      visitIdentifier(it)
    }
    walkQualifiedPath(path)
  }

  override fun visitIdentifier(identifier: Identifier) {
    walkIdentifier(identifier)
  }

  override fun visitNoBody(body: NoBody) {
    walkNoBody(body)
  }

  override fun visitExprBody(body: ExprBody) {
    visitExpr(body.expr)
    walkExprBody(body)
  }

  override fun visitCodeBody(body: CodeBody) {
    visitStmts(body.stmts)
    body.value?.let { visitExpr(it) }
    walkCodeBody(body)
  }

  override fun visitBlockExpr(expr: BlockExpr) {
    visitStmts(expr.stmts)
    expr.value?.let { visitExpr(it) }
    walkBlockExpr(expr)
  }

  override fun visitThenBranch(branch: ThenBranch) {
    visitExpr(branch.value)
    walkThenBranch(branch)
  }

  override fun visitBlockBranch(branch: BlockBranch) {
    visitStmts(branch.stmts)
    branch.value?.let { visitExpr(it) }
    walkBlockBranch(branch)
  }
}
