package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.element.* // ktlint-disable no-unused-imports
import com.gabrielleeg1.plank.grammar.element.*
import com.gabrielleeg1.plank.shared.depthFirstSearch
import pw.binom.Stack

/**
 * Analyzes the provided [PlankFile] and returns a typed [ResolvedPlankFile]
 * with typed declarations/statements/expressions.
 */
fun analyze(file: PlankFile, tree: ModuleTree): ResolvedPlankFile {
  return BindingContext(tree).analyze(file)
}

@Suppress("UnusedPrivateMember")
internal class BindingContext(tree: ModuleTree) :
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<ResolvedStmt>,
  Pattern.Visitor<TypedPattern>,
  PlankFile.Visitor<ResolvedPlankFile>,
  TypeRef.Visitor<PlankType> {
  /**
   * Used for type inference, where the type-system mutate the element's type to the most useful.
   *
   * E.G in pseudocode:
   * ```reasonml
   * val (+): int => int => int;
   *
   * // the use of x in the binary operation that references on [x] function
   * // will mutate the type in the binding map to get the more acceptable
   * // type there.
   * let f = (x) => x + 1;
   * ```
   */
  // TODO: type inference
  private val bindings = mutableMapOf<Expr, PlankType>()

  private val scopes = Stack<Scope>().also { stack ->
    stack.pushLast(GlobalScope(tree))
  }

  var violations = emptySet<BindingViolation>()

  // TODO: testing
  @Suppress("unused")
  val isValid
    get() = violations.isEmpty()

  fun analyze(file: PlankFile): ResolvedPlankFile {
    val globalScope = currentScope
    val fileModule = currentModuleTree
      .createModule(file.module, globalScope, file.program)
      .apply {
        scope = FileScope(file, globalScope)
      }

    return file
      .searchDependencies(file.module)
      .also { scopes.pushLast(fileModule.scope) }
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
      .asReversed()
      .map(this::visit)
      .let { dependencies ->
        dependencies
          .last()
          .copy(dependencies = dependencies.take(dependencies.size - 1))
      }
  }

  private fun PlankFile.searchDependencies(name: Identifier): List<Module> {
    return currentModuleTree.dependencies
      .apply {
        addVertex(name)

        val dependencyTreeWalker = object : TreeWalker() {
          override fun visitImportDecl(decl: ImportDecl) {
            addEdge(name, decl.path.toIdentifier())
          }
        }

        dependencyTreeWalker.walk(this@searchDependencies)
      }
      .depthFirstSearch(name)
      .mapNotNull(currentModuleTree::findModule)
  }

  override fun visitPlankFile(file: PlankFile): ResolvedPlankFile {
    val program = visitStmts(file.program).filterIsInstance<ResolvedDecl>()

    return ResolvedPlankFile(file, program, bindingViolations = violations.toList())
  }

  override fun visitConstExpr(expr: ConstExpr): TypedExpr {
    val type = when (expr.value) {
      is Boolean -> BoolType
      is Unit -> BoolType
      is String -> PointerType(CharType)
      is Int -> IntType(32)
      is Short -> IntType(16)
      is Byte -> IntType(8)
      is Double -> FloatType(32)
      is Long, Float -> FloatType(64)
      else -> return violate("Unknown type %s", expr.value::class.simpleName)
    }

    return TypedConstExpr(expr.value, type, expr.location)
  }

  override fun visitAccessExpr(expr: AccessExpr): TypedExpr {
    val variable = findVariable(expr.path.toIdentifier())

    if (
      !variable.isInScope &&
      variable.declaredIn !is FileScope &&
      variable.declaredIn !is GlobalScope
    ) {
      (currentScope as? FunctionScope)?.apply {
        references[variable.name] = variable
      }
    }

    return TypedAccessExpr(variable, expr.location)
  }

  override fun visitCallExpr(expr: CallExpr): TypedExpr {
    val callable = visit(expr.callee)

    val function = callable.type.cast<FunctionType>()
      ?: return violate("Can not call not function %s", callable)

    return function.call(callable, expr.location, visitExprs(expr.arguments))
  }

  override fun visitAssignExpr(expr: AssignExpr): TypedExpr {
    val reference = findVariable(expr.name)
    val value = visit(expr.value)

    if (!reference.mutable) {
      return violate("Can not change immutable variable %s", reference)
    }

    if (reference.value.type != value.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in assignment", reference.value.type, value.type
      )
    }

    reference.value = value

    return TypedAssignExpr(reference.name, value, value.type, expr.location)
  }

  override fun visitSetExpr(expr: SetExpr): TypedExpr {
    val receiver = visit(expr.receiver)
    val value = visit(expr.value)

    val struct = receiver.type.cast<StructType>()
      ?: return violate("Can not update a property from non-struct expression %s", receiver)

    val property = struct.property(expr.property)
      ?: return violate("Unknown property %s in struct %s", expr.property, struct)

    if (property.type != value.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in set expression", property.type, value.type
      )
    }

    return TypedSetExpr(receiver, property.name, value, value.type, expr.location)
  }

  override fun visitGetExpr(expr: GetExpr): TypedExpr {
    val receiver = visit(expr.receiver)

    val struct = receiver.type.cast<StructType>()
      ?: return violate("Can not get a property from non-struct expression %s", receiver)

    val property = struct.property(expr.property)
      ?: return violate("Unknown property %s in struct %s", expr.property, struct)

    return TypedGetExpr(receiver, property.name, property.type, expr.location)
  }

  override fun visitGroupExpr(expr: GroupExpr): TypedExpr {
    val inner = visit(expr.value)

    return TypedGroupExpr(inner, inner.location)
  }

  override fun visitInstanceExpr(expr: InstanceExpr): TypedExpr {
    val struct = visit(expr.type).cast<StructType> {
      return violate("Can not get a instance from non-struct type %s", it)
    }

    val arguments = expr.arguments.mapValues { (name, expr) ->
      val value = visit(expr)
      val property = struct.property(name)
        ?: return violate("Unknown property %s in struct %s", name, struct)

      if (property.type != value.type) {
        return violate(
          "Mismatch types, expecting %s but got %s in set expression",
          property.type, value.type
        )
      }

      value
    }

    return struct.instantiate(expr.location, arguments)
  }

  override fun visitSizeofExpr(expr: SizeofExpr): TypedExpr {
    val type = visit(expr.type)

    return IntType(32).const(type.size)
  }

  override fun visitRefExpr(expr: RefExpr): TypedExpr {
    val inner = visit(expr.expr)

    return TypedRefExpr(inner, inner.location)
  }

  override fun visitDerefExpr(expr: DerefExpr): TypedExpr {
    val ref = visit(expr.ref)

    val type = ref.type.cast<PointerType> {
      return violate("Can not deref from non-pointer type %s", it)
    }

    return TypedDerefExpr(ref, type.inner, expr.location)
  }

  override fun visitErrorExpr(expr: ErrorExpr): TypedExpr {
    return TypedErrorExpr(expr.message, expr.arguments, expr.location)
  }

  override fun visitMatchExpr(expr: MatchExpr): TypedExpr {
    val subject = visit(expr.subject)

    val patterns = expr.patterns
      .entries
      .associate { (pattern, value) ->
        visit(pattern) to scoped(ClosureScope(Identifier("match"), currentScope)) {
          deconstruct(pattern, subject)

          visit(value)
        }
      }

    val type = patterns.values.map { it.type }.reduce { acc, next ->
      if (acc != next) {
        return violate("Mismatch types, expecting %s but got %s in match expression", acc, next)
      }

      next
    }

    return TypedMatchExpr(subject, patterns, type, expr.location)
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): TypedPattern {
    TODO()
  }

  override fun visitIdentPattern(pattern: IdentPattern): TypedPattern {
    return TypedIdentPattern(pattern.name, Untyped, pattern.location)
  }

  override fun visitIfExpr(expr: IfExpr): TypedExpr {
    val cond = visit(expr.cond)

    if (cond.type != BoolType) {
      return violate("Mismatch types, expecting bool value but got %s in if condition", cond)
    }

    val thenBranch = visit(expr.thenBranch)
    val elseBranch = expr.elseBranch?.let { visit(it) }

    if (elseBranch == null) {
      return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, expr.location)
    }

    if (thenBranch.type != elseBranch.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in if expression",
        thenBranch.type,
        elseBranch.type
      )
    }

    return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, expr.location)
  }

  override fun visitExprStmt(stmt: ExprStmt): ResolvedStmt {
    return ResolvedExprStmt(visit(stmt.expr), stmt.location)
  }

  override fun visitReturnStmt(stmt: ReturnStmt): ResolvedStmt {
    val expr = stmt.value?.let(::visit) ?: UnitType.const()

    val functionScope = currentScope as? FunctionScope
      ?: return violate("Can not return in not function scope %s", currentScope).stmt()

    if (functionScope.returnType != expr.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in return statement",
        functionScope.returnType, expr.type
      ).stmt()
    }

    return ResolvedReturnStmt(expr, stmt.location)
  }

  override fun visitErrorStmt(stmt: ErrorStmt): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitImportDecl(decl: ImportDecl): ResolvedStmt {
    val module = currentScope.findModule(decl.path.toIdentifier())
      ?: return violate("Unresolved module %s", decl.path.toIdentifier()).stmt()

    return ResolvedImportDecl(module, decl.location)
  }

  override fun visitModuleDecl(decl: ModuleDecl): ResolvedStmt {
    val module = currentModuleTree.createModule(
      name = decl.path.toIdentifier(),
      enclosing = currentScope,
      content = decl.content,
    )
    val content = scoped(module.scope) {
      visitStmts(decl.content).filterIsInstance<ResolvedDecl>()
    }

    return ResolvedModuleDecl(decl.path, content, decl.location)
  }

  override fun visitEnumDecl(decl: EnumDecl): ResolvedStmt {
    val name = decl.name

    val enum = DelegateType(PointerType(EnumType(name))).also {
      currentScope.create(name, it)
    }

    val members = decl.members.associate { (name, parameters) ->
      val types = visitTypeRefs(parameters)

      currentScope.declare(name, FunctionType(enum, types))

      name to EnumMember(name, types)
    }

    enum.value = EnumType(name, members)

    return ResolvedEnumDecl(name, members, enum, decl.location)
  }

  override fun visitStructDecl(decl: StructDecl): ResolvedStmt {
    val name = decl.name

    val struct = DelegateType(StructType(decl.name)).also {
      currentScope.create(name, it)
    }

    val properties = decl.properties.associate { (mutable, name, type) ->
      name to StructProperty(mutable, name, visit(type))
    }

    struct.value = StructType(name, properties)

    return ResolvedStructDecl(name, properties, struct, decl.location)
  }

  override fun visitFunDecl(decl: FunDecl): ResolvedStmt {
    val name = decl.name
    val realParameters = decl.realParameters.mapValues { closureIfFunction(visit(it.value)) }

    val location = decl.location

    val attributes = decl.attributes // todo validate

    val type = visitFunctionTypeRef(decl.type)

    currentScope.declare(name, type, decl.location)

    val scope = FunctionScope(type, name, currentScope, currentModuleTree)
    val content = scoped(name, scope) {
      decl.realParameters
        .mapKeys { it.key }
        .forEach { (name, type) ->
          declare(name, closureIfFunction(visit(type)))
        }

      visitStmts(decl.body)
    }
    val references = scope.references.mapValuesTo(LinkedHashMap()) { (_, variable) ->
      variable.value.type
    }

    return ResolvedFunDecl(name, content, realParameters, attributes, references, type, location)
  }

  override fun visitLetDecl(decl: LetDecl): ResolvedStmt {
    val name = decl.name
    val mutable = decl.mutable
    val value = visit(decl.value)

    currentScope.declare(name, value, mutable)

    // TODO: use explicit type to infer with [bindings]
    return ResolvedLetDecl(name, mutable, value, value.type, decl.location)
  }

  override fun visitErrorDecl(decl: ErrorDecl): ResolvedStmt {
    return ResolvedErrorDecl(decl.message, decl.arguments, decl.location)
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): PlankType {
    return currentScope.findType(ref.path.toIdentifier())
      ?: return violate("Unresolved type reference to %s", ref.path.toIdentifier()).type
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): PlankType {
    return PointerType(visit(ref.type))
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef): PlankType {
    return ArrayType(visit(ref.type))
  }

  // TODO: infer if hasn't user-defined type
  override fun visitFunctionTypeRef(ref: FunctionTypeRef): FunctionType {
    val parameter = visit(ref.parameter)
    val returnType = visit(ref.returnType) { UnitType }

    val actualReturnType = visit(ref.actualReturnType) { UnitType }
    val realParameters = ref.realParameters.mapValues { visit(it.value) }

    return FunctionType(
      parameter,
      returnType,
      actualReturnType,
      realParameters,
      isClosure = if (ref.isClosure == null) !currentScope.isTopLevelScope else ref.isClosure!!
    )
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef): PlankType {
    return UnitType
  }

  //
  // Utils for the type system
  //
  private fun Scope.deconstruct(
    pattern: Pattern,
    subject: TypedExpr,
    type: PlankType = subject.type
  ) {
    when (pattern) {
      is IdentPattern -> {
        type.cast<EnumType>()?.member(pattern.name) ?: return declare(pattern.name, subject)
      }
      is NamedTuplePattern -> {
        val enum = subject.type.cast<EnumType>() ?: return run {
          violate(
            "Expecting enum when deconstructing named tuple in pattern but got %s", subject.type
          )
        }

        val member = enum.member(pattern.type.toIdentifier()) ?: return run {
          violate("Unknown enum member of %s: %s", enum.name, subject.type)
        }

        pattern.fields.forEachIndexed { index, subPattern ->
          val subType = member.fields.getOrNull(index) ?: return run {
            violate("Unknown property %d of member %s", index, member.name)
          }

          deconstruct(subPattern, undeclared(subType), enum)
        }
      }
    }
  }

  private fun violate(message: String, vararg values: Any?): TypedExpr {
    violations = violations + BindingViolation(message, values.toList())

    return TypedConstExpr(Unit, Untyped, Location.Generated)
  }

  private fun undeclared(type: PlankType): TypedExpr {
    return TypedConstExpr(Unit, type, Location.Generated)
  }

  private val attributeScope = AttributeScope(tree)

  private val currentScope get() = scopes.peekLast()
  private val currentModuleTree get() = scopes.peekLast().moduleTree

  private fun closureIfFunction(type: PlankType): PlankType {
    return when (type) {
      is FunctionType -> type.copy(isClosure = true)
      else -> type
    }
  }

  private fun findVariable(name: Identifier): Variable {
    return currentScope.findVariable(name)
      ?: Variable(false, name, violate("Unknown variable", name), currentScope)
  }

  private inline fun <T> scoped(scope: Scope, body: Scope.() -> T): T {
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }

  private inline fun <T> scoped(
    name: Identifier = Identifier("anonymous"),
    scope: Scope = ClosureScope(name, scopes.peekLast()),
    body: Scope.() -> T
  ): T {
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }
}
