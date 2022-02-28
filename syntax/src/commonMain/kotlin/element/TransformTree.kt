package org.plank.syntax.element

import org.plank.shared.id

fun <A : SimplePlankElement> transformTree(
  value: A,
  enterExpr: (Expr) -> Expr = ::id,
  exitExpr: (Expr) -> Expr = ::id,
  enterStmt: (Stmt) -> Stmt = ::id,
  exitStmt: (Stmt) -> Stmt = ::id,
  enterBranch: (IfBranch) -> IfBranch = ::id,
  exitBranch: (IfBranch) -> IfBranch = ::id,
  enterBody: (FunctionBody) -> FunctionBody = ::id,
  exitBody: (FunctionBody) -> FunctionBody = ::id,
  enterPattern: (Pattern) -> Pattern = ::id,
  exitPattern: (Pattern) -> Pattern = ::id,
  enterTypeRef: (TypeRef) -> TypeRef = ::id,
  exitTypeRef: (TypeRef) -> TypeRef = ::id,
): A {
  lateinit var visitExpr: (Expr) -> Expr
  lateinit var visitStmt: (Stmt) -> Stmt
  lateinit var visitBranch: (IfBranch) -> IfBranch
  lateinit var visitBody: (FunctionBody) -> FunctionBody
  lateinit var visitPattern: (Pattern) -> Pattern
  lateinit var visitTypeRef: (TypeRef) -> TypeRef

  fun visitExpr(value: Expr): Expr = when (val expr = enterExpr(value)) {
    is ConstExpr -> exitExpr(expr)
    is AccessExpr -> exitExpr(expr)
    is GroupExpr -> exitExpr(expr.copy(value = visitExpr(expr.value)))
    is AssignExpr -> exitExpr(expr.copy(value = visitExpr(expr.value)))
    is RefExpr -> exitExpr(expr.copy(value = visitExpr(expr.value)))
    is DerefExpr -> exitExpr(expr.copy(value = visitExpr(expr.value)))
    is GetExpr -> exitExpr(expr.copy(receiver = visitExpr(expr.receiver), property = expr.property))
    is SizeofExpr -> exitExpr(expr.copy(type = visitTypeRef(expr.type)))
    is BlockExpr -> exitExpr(
      expr.copy(stmts = expr.stmts.map(visitStmt), value = expr.value?.let { visitExpr(it) })
    )
    is CallExpr -> exitExpr(
      expr.copy(callee = visitExpr(expr.callee), arguments = expr.arguments.map(visitExpr)),
    )
    is InstanceExpr -> exitExpr(
      expr.copy(
        type = visitTypeRef(expr.type),
        arguments = expr.arguments.mapValues { visitExpr(it.value) },
      ),
    )
    is SetExpr -> exitExpr(
      expr.copy(
        receiver = visitExpr(expr.receiver),
        value = visitExpr(expr.value),
        property = expr.property,
      ),
    )
    is IfExpr -> exitExpr(
      expr.copy(
        cond = visitExpr(expr.cond),
        thenBranch = visitBranch(expr.thenBranch),
        elseBranch = expr.elseBranch?.let { visitBranch(it) },
      ),
    )
    is MatchExpr -> exitExpr(
      expr.copy(
        subject = visitExpr(expr.subject),
        patterns = expr.patterns
          .map { (pattern, value) -> visitPattern(pattern) to visitExpr(value) }
          .toMap(),
      ),
    )
  }

  fun visitStmt(value: Stmt): Stmt = when (val stmt = enterStmt(value)) {
    is ExprStmt -> exitStmt(stmt.copy(expr = visitExpr(stmt.expr)))
    is ReturnStmt -> exitStmt(stmt.copy(value = stmt.value?.let(visitExpr)))
    is UseDecl -> exitStmt(stmt)
    is ModuleDecl -> exitStmt(
      stmt.copy(content = stmt.content.map(visitStmt).filterIsInstance<Decl>()),
    )
    is LetDecl -> exitStmt(
      stmt.copy(type = stmt.type?.let(visitTypeRef), value = visitExpr(stmt.value))
    )
    is StructDecl -> exitStmt(
      stmt.copy(
        properties = stmt.properties.map { it.copy(type = visitTypeRef(it.type)) },
      )
    )
    is EnumDecl -> exitStmt(
      stmt.copy(
        members = stmt.members.map { it.copy(parameters = it.parameters.map(visitTypeRef)) },
      ),
    )
    is FunDecl -> exitStmt(
      stmt.copy(
        body = visitBody(stmt.body),
        parameters = stmt.parameters.mapValues { visitTypeRef(it.value) },
        returnType = visitTypeRef(stmt.returnType),
      )
    )
  }

  fun visitBranch(value: IfBranch): IfBranch = when (val branch = enterBranch(value)) {
    is ThenBranch -> exitBranch(branch.copy(value = visitExpr(branch.value)))
    is BlockBranch -> exitBranch(
      branch.copy(stmts = branch.stmts.map(visitStmt), value = branch.value?.let(visitExpr)),
    )
  }

  fun visitBody(value: FunctionBody): FunctionBody = when (val body = enterBody(value)) {
    is NoBody -> exitBody(body)
    is ExprBody -> exitBody(body.copy(expr = visitExpr(body.expr)))
    is CodeBody -> exitBody(
      body.copy(stmts = body.stmts.map(visitStmt), value = body.value?.let(visitExpr)),
    )
  }

  fun visitPattern(value: Pattern): Pattern = when (val pattern = enterPattern(value)) {
    is IdentPattern -> exitPattern(pattern)
    is EnumVariantPattern -> exitPattern(
      pattern.copy(properties = pattern.properties.map(visitPattern)),
    )
  }

  fun visitTypeRef(value: TypeRef): TypeRef = when (val ref = enterTypeRef(value)) {
    is AccessTypeRef -> exitTypeRef(ref)
    is GenericTypeRef -> exitTypeRef(ref)
    is UnitTypeRef -> exitTypeRef(ref)
    is PointerTypeRef -> exitTypeRef(ref.copy(type = visitTypeRef(ref.type)))
    is ApplyTypeRef -> exitTypeRef(
      ref.copy(
        function = visitTypeRef(ref.function),
        arguments = ref.arguments.map(visitTypeRef),
      ),
    )
    is FunctionTypeRef -> exitTypeRef(
      ref.copy(
        returnType = visitTypeRef(ref.returnType),
        parameterType = visitTypeRef(ref.parameterType),
      ),
    )
  }

  fun visitPlankFile(file: PlankFile): PlankFile {
    return file.copy(program = file.program.map(visitStmt).filterIsInstance<Decl>())
  }

  visitExpr = ::visitExpr
  visitStmt = ::visitStmt
  visitBranch = ::visitBranch
  visitBody = ::visitBody
  visitPattern = ::visitPattern
  visitTypeRef = ::visitTypeRef

  @Suppress("UNCHECKED_CAST")
  return when (value) {
    is Expr -> visitExpr(value) as A
    is Stmt -> visitStmt(value) as A
    is IfBranch -> visitBranch(value) as A
    is FunctionBody -> visitBody(value) as A
    is Pattern -> visitPattern(value) as A
    is TypeRef -> visitTypeRef(value) as A
    is PlankFile -> visitPlankFile(value) as A
    else -> error("Unsupported type ${value::class}")
  }
}
