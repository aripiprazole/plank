package org.plank.syntax.element

@Suppress("EmptyFunctionBlock", "TooManyFunctions")
open class TreeTransformer :
  Expr.Visitor<Expr>,
  Stmt.Visitor<Stmt>,
  Pattern.Visitor<Pattern>,
  QualifiedPath.Visitor<QualifiedPath>,
  Identifier.Visitor<Identifier>,
  TypeRef.Visitor<TypeRef>,
  FunctionBody.Visitor<FunctionBody>,
  IfBranch.Visitor<IfBranch>,
  PlankFile.Visitor<PlankFile> {
  fun transform(element: PlankElement): PlankElement = when (element) {
    is Expr -> visitExpr(element)
    is Stmt -> visitStmt(element)
    is TypeRef -> visitTypeRef(element)
    is PlankFile -> visitPlankFile(element)
    else -> error("Could not visit ${element::class}")
  }

  open fun transformPlankFile(file: PlankFile): PlankFile {
    return file
  }

  open fun transformBlockExpr(expr: BlockExpr): Expr {
    return expr
  }

  open fun transformMatchExpr(expr: MatchExpr): Expr {
    return expr
  }

  open fun transformIfExpr(expr: IfExpr): Expr {
    return expr
  }

  open fun transformConstExpr(expr: ConstExpr): Expr {
    return expr
  }

  open fun transformAccessExpr(expr: AccessExpr): Expr {
    return expr
  }

  open fun transformCallExpr(expr: CallExpr): Expr {
    return expr
  }

  open fun transformAssignExpr(expr: AssignExpr): Expr {
    return expr
  }

  open fun transformSetExpr(expr: SetExpr): Expr {
    return expr
  }

  open fun transformGetExpr(expr: GetExpr): Expr {
    return expr
  }

  open fun transformGroupExpr(expr: GroupExpr): Expr {
    return expr
  }

  open fun transformInstanceExpr(expr: InstanceExpr): Expr {
    return expr
  }

  open fun transformSizeofExpr(expr: SizeofExpr): Expr {
    return expr
  }

  open fun transformRefExpr(expr: RefExpr): Expr {
    return expr
  }

  open fun transformDerefExpr(expr: DerefExpr): Expr {
    return expr
  }

  open fun transformNoBody(body: NoBody): FunctionBody {
    return body
  }

  open fun transformExprBody(body: ExprBody): FunctionBody {
    return body
  }

  open fun transformCodeBody(body: CodeBody): FunctionBody {
    return body
  }

  open fun transformIdentifier(identifier: Identifier): Identifier {
    return identifier
  }

  open fun transformThenBranch(branch: ThenBranch): IfBranch {
    return branch
  }

  open fun transformBlockBranch(branch: BlockBranch): IfBranch {
    return branch
  }

  open fun transformNamedTuplePattern(pattern: NamedTuplePattern): Pattern {
    return pattern
  }

  open fun transformIdentPattern(pattern: IdentPattern): Pattern {
    return pattern
  }

  open fun transformQualifiedPath(path: QualifiedPath): QualifiedPath {
    return path
  }

  open fun transformExprStmt(stmt: ExprStmt): Stmt {
    return stmt
  }

  open fun transformReturnStmt(stmt: ReturnStmt): Stmt {
    return stmt
  }

  open fun transformUseDecl(decl: UseDecl): Decl {
    return decl
  }

  open fun transformModuleDecl(decl: ModuleDecl): Decl {
    return decl
  }

  open fun transformEnumDecl(decl: EnumDecl): Decl {
    return decl
  }

  open fun transformStructDecl(decl: StructDecl): Decl {
    return decl
  }

  open fun transformFunDecl(decl: FunDecl): Decl {
    return decl
  }

  open fun transformLetDecl(decl: LetDecl): Decl {
    return decl
  }

  open fun transformAccessTypeRef(ref: AccessTypeRef): TypeRef {
    return ref
  }

  open fun transformGenericTypeRef(ref: GenericTypeRef): TypeRef {
    return ref
  }

  open fun transformPointerTypeRef(ref: PointerTypeRef): TypeRef {
    return ref
  }

  open fun transformApplyTypeRef(ref: ApplyTypeRef): TypeRef {
    return ref
  }

  open fun transformFunctionTypeRef(ref: FunctionTypeRef): TypeRef {
    return ref
  }

  open fun transformUnitTypeRef(ref: UnitTypeRef): TypeRef {
    return ref
  }

  override fun visitPlankFile(file: PlankFile): PlankFile {
    return transformPlankFile(
      file.copy(program = visitStmts(file.program).filterIsInstance<Decl>()),
    )
  }

  override fun visitMatchExpr(expr: MatchExpr): Expr {
    return transformMatchExpr(
      expr.copy(
        subject = visitExpr(expr.subject),
        patterns = expr.patterns.map { (pattern, value) ->
          visitPattern(pattern) to visitExpr(value)
        }.toMap(),
      ),
    )
  }

  override fun visitIfExpr(expr: IfExpr): Expr {
    return transformIfExpr(
      expr.copy(
        cond = visitExpr(expr.cond),
        thenBranch = visitIfBranch(expr.thenBranch),
        elseBranch = expr.elseBranch?.let { visitIfBranch(it) },
      ),
    )
  }

  override fun visitConstExpr(expr: ConstExpr): Expr {
    return transformConstExpr(expr)
  }

  override fun visitAccessExpr(expr: AccessExpr): Expr {
    return transformAccessExpr(expr.copy(name = visitIdentifier(expr.name)))
  }

  override fun visitCallExpr(expr: CallExpr): Expr {
    return transformCallExpr(
      expr.copy(
        callee = visitExpr(expr.callee),
        arguments = visitExprs(expr.arguments),
      ),
    )
  }

  override fun visitAssignExpr(expr: AssignExpr): Expr {
    return transformAssignExpr(
      expr.copy(
        name = visitIdentifier(expr.name),
        value = visitExpr(expr.value),
      )
    )
  }

  override fun visitSetExpr(expr: SetExpr): Expr {
    return transformSetExpr(
      expr.copy(
        receiver = visitExpr(expr.receiver),
        property = visitIdentifier(expr.property),
        value = visitExpr(expr.value),
      ),
    )
  }

  override fun visitGetExpr(expr: GetExpr): Expr {
    return transformGetExpr(
      expr.copy(
        receiver = visitExpr(expr.receiver),
        property = visitIdentifier(expr.property),
      ),
    )
  }

  override fun visitGroupExpr(expr: GroupExpr): Expr {
    return transformGroupExpr(expr.copy(value = visitExpr(expr.value)))
  }

  override fun visitInstanceExpr(expr: InstanceExpr): Expr {
    return transformInstanceExpr(
      expr.copy(
        type = visitTypeRef(expr.type),
        arguments = expr.arguments.map { (property, value) ->
          visitIdentifier(property) to visitExpr(value)
        }.toMap(),
      ),
    )
  }

  override fun visitSizeofExpr(expr: SizeofExpr): Expr {
    return transformSizeofExpr(expr.copy(type = visitTypeRef(expr.type)))
  }

  override fun visitRefExpr(expr: RefExpr): Expr {
    return transformRefExpr(expr.copy(value = visitExpr(expr.value)))
  }

  override fun visitDerefExpr(expr: DerefExpr): Expr {
    return transformDerefExpr(expr.copy(value = visitExpr(expr.value)))
  }

  override fun visitExprStmt(stmt: ExprStmt): Stmt {
    return transformExprStmt(stmt.copy(expr = visitExpr(stmt.expr)))
  }

  override fun visitReturnStmt(stmt: ReturnStmt): Stmt {
    return transformReturnStmt(stmt.copy(value = stmt.value?.let { visitExpr(it) }))
  }

  override fun visitUseDecl(decl: UseDecl): Decl {
    return transformUseDecl(decl.copy(path = visitQualifiedPath(decl.path)))
  }

  override fun visitModuleDecl(decl: ModuleDecl): Decl {
    return transformModuleDecl(
      decl.copy(
        path = visitQualifiedPath(decl.path),
        content = visitStmts(decl.content).filterIsInstance<Decl>(),
      ),
    )
  }

  override fun visitEnumDecl(decl: EnumDecl): Decl {
    return transformEnumDecl(
      decl.copy(
        name = visitIdentifier(decl.name),
        members = decl.members.map { member ->
          val (name, parameters) = member

          member.copy(name = visitIdentifier(name), parameters = visitTypeRefs(parameters))
        },
      ),
    )
  }

  override fun visitStructDecl(decl: StructDecl): Decl {
    return transformStructDecl(
      decl.copy(
        name = visitIdentifier(decl.name),
        properties = decl.properties.map { member ->
          val (_, name, type) = member

          member.copy(name = visitIdentifier(name), type = visitTypeRef(type))
        },
      ),
    )
  }

  override fun visitFunDecl(decl: FunDecl): Decl {
    return transformFunDecl(
      decl.copy(
        name = visitIdentifier(decl.name),
        body = visitFunctionBody(decl.body),
        parameters = decl.parameters
          .map { (parameter, type) -> visitIdentifier(parameter) to visitTypeRef(type) }
          .toMap(),
        returnType = visitTypeRef(decl.returnType)
      ),
    )
  }

  override fun visitLetDecl(decl: LetDecl): Decl {
    return transformLetDecl(
      decl.copy(
        name = visitIdentifier(decl.name),
        type = decl.type?.let { visitTypeRef(it) },
        value = visitExpr(decl.value),
      ),
    )
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): TypeRef {
    return transformAccessTypeRef(ref.copy(path = visitQualifiedPath(ref.path)))
  }

  override fun visitGenericTypeRef(ref: GenericTypeRef): TypeRef {
    return transformGenericTypeRef(ref.copy(name = visitIdentifier(ref.name)))
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): TypeRef {
    return transformPointerTypeRef(ref.copy(type = visitTypeRef(ref.type)))
  }

  override fun visitApplyTypeRef(ref: ApplyTypeRef): TypeRef {
    return transformApplyTypeRef(
      ref.copy(
        function = visitTypeRef(ref.function),
        arguments = visitTypeRefs(ref.arguments),
      ),
    )
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef): TypeRef {
    return transformFunctionTypeRef(
      ref.copy(
        parameterType = visitTypeRef(ref.parameterType),
        returnType = visitTypeRef(ref.returnType),
      ),
    )
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef): TypeRef {
    return transformUnitTypeRef(ref)
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): Pattern {
    return transformNamedTuplePattern(
      pattern.copy(
        type = visitQualifiedPath(pattern.type),
        properties = visitPatterns(pattern.properties),
      ),
    )
  }

  override fun visitIdentPattern(pattern: IdentPattern): Pattern {
    return transformIdentPattern(pattern.copy(name = visitIdentifier(pattern.name)))
  }

  override fun visitQualifiedPath(path: QualifiedPath): QualifiedPath {
    return transformQualifiedPath(path.copy(fullPath = path.fullPath.map { visitIdentifier(it) }))
  }

  override fun visitIdentifier(identifier: Identifier): Identifier {
    return transformIdentifier(identifier)
  }

  override fun visitNoBody(body: NoBody): FunctionBody {
    return transformNoBody(body)
  }

  override fun visitExprBody(body: ExprBody): FunctionBody {
    return transformExprBody(body.copy(expr = visitExpr(body.expr)))
  }

  override fun visitCodeBody(body: CodeBody): FunctionBody {
    return transformCodeBody(
      body.copy(
        stmts = visitStmts(body.stmts),
        value = body.value?.let { visitExpr(it) },
      ),
    )
  }

  override fun visitBlockExpr(expr: BlockExpr): Expr {
    return transformBlockExpr(
      expr.copy(
        stmts = visitStmts(expr.stmts),
        value = expr.value?.let { visitExpr(it) },
      ),
    )
  }

  override fun visitThenBranch(branch: ThenBranch): IfBranch {
    return transformThenBranch(branch.copy(value = visitExpr(branch.value)))
  }

  override fun visitBlockBranch(branch: BlockBranch): IfBranch {
    return transformBlockBranch(
      branch.copy(
        stmts = visitStmts(branch.stmts),
        value = branch.value?.let { visitExpr(it) },
      ),
    )
  }
}
