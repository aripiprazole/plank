package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.PlankType.Companion.array
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.bool
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.char
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.delegate
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.enum
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.float
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.function
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.int
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.pointer
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.struct
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.unit
import com.gabrielleeg1.plank.analyzer.PlankType.Companion.untyped
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedConstExpr
import com.gabrielleeg1.plank.analyzer.element.ResolvedDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedErrorDecl
import com.gabrielleeg1.plank.analyzer.element.TypedErrorExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.ResolvedExprStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.TypedGroupExpr
import com.gabrielleeg1.plank.analyzer.element.TypedIdentPattern
import com.gabrielleeg1.plank.analyzer.element.TypedIfExpr
import com.gabrielleeg1.plank.analyzer.element.ResolvedImportDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedLetDecl
import com.gabrielleeg1.plank.analyzer.element.TypedMatchExpr
import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.analyzer.element.TypedPattern
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.analyzer.element.TypedReferenceExpr
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedStructDecl
import com.gabrielleeg1.plank.grammar.element.AccessExpr
import com.gabrielleeg1.plank.grammar.element.AccessTypeRef
import com.gabrielleeg1.plank.grammar.element.ArrayTypeRef
import com.gabrielleeg1.plank.grammar.element.AssignExpr
import com.gabrielleeg1.plank.grammar.element.CallExpr
import com.gabrielleeg1.plank.grammar.element.ConstExpr
import com.gabrielleeg1.plank.grammar.element.EnumDecl
import com.gabrielleeg1.plank.grammar.element.ErrorDecl
import com.gabrielleeg1.plank.grammar.element.ErrorExpr
import com.gabrielleeg1.plank.grammar.element.ErrorStmt
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.ExprStmt
import com.gabrielleeg1.plank.grammar.element.FunDecl
import com.gabrielleeg1.plank.grammar.element.FunctionTypeRef
import com.gabrielleeg1.plank.grammar.element.GetExpr
import com.gabrielleeg1.plank.grammar.element.IdentPattern
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.IfExpr
import com.gabrielleeg1.plank.grammar.element.ImportDecl
import com.gabrielleeg1.plank.grammar.element.InstanceExpr
import com.gabrielleeg1.plank.grammar.element.LetDecl
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.MatchExpr
import com.gabrielleeg1.plank.grammar.element.ModuleDecl
import com.gabrielleeg1.plank.grammar.element.NamedTuplePattern
import com.gabrielleeg1.plank.grammar.element.Pattern
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.element.PointerTypeRef
import com.gabrielleeg1.plank.grammar.element.RefExpr
import com.gabrielleeg1.plank.grammar.element.ReturnStmt
import com.gabrielleeg1.plank.grammar.element.SetExpr
import com.gabrielleeg1.plank.grammar.element.SizeofExpr
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.grammar.element.StructDecl
import com.gabrielleeg1.plank.grammar.element.TypeRef
import com.gabrielleeg1.plank.grammar.element.visit
import com.gabrielleeg1.plank.grammar.tree.TreeWalker
import com.gabrielleeg1.plank.shared.depthFirstSearch
import pw.binom.Stack

/**
 * This is the type-system core.
 *
 * This will map the file provided in [analyze] with the provided tree
 * into a typed one [ResolvedPlankFile] with typed declarations/statements/expressions.
 */
interface BindingContext :
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<ResolvedStmt>,
  Pattern.Visitor<TypedPattern>,
  PlankFile.Visitor<ResolvedPlankFile>,
  TypeRef.Visitor<PlankType> {
  val violations: Set<BindingViolation>
  val isValid: Boolean

  fun analyze(file: PlankFile): ResolvedPlankFile
}

@Suppress("Detekt.FunctionNaming")
fun BindingContext(tree: ModuleTree): BindingContext = BindingContextImpl(tree)

private class BindingContextImpl(tree: ModuleTree) : BindingContext {
  /**
   * Used for type inference, where the type-system mutate the element's type to the most useful.
   *
   * E.G in pseudo-code:
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

  override var violations = emptySet<BindingViolation>()

  override val isValid get() = violations.isEmpty()

  override fun analyze(file: PlankFile): ResolvedPlankFile {
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
            addEdge(name, decl.module)
          }
        }

        dependencyTreeWalker.walk(this@searchDependencies)
      }
      .depthFirstSearch(name)
      .mapNotNull(currentModuleTree::findModule)
  }

  override fun visitPlankFile(file: PlankFile): ResolvedPlankFile {
    val program = visit(file.program).filterIsInstance<ResolvedDecl>()

    return ResolvedPlankFile(file, program)
  }

  override fun visitConstExpr(expr: ConstExpr): TypedExpr {
    val type = when (expr.value) {
      is Boolean -> bool
      is Unit -> bool
      is String -> pointer(char)
      is Int -> int(32)
      is Short -> int(16)
      is Byte -> int(8)
      is Double -> float(32)
      is Long, Float -> float(64)
      else -> return violate("Unknown type %s", expr::class.simpleName)
    }

    return TypedConstExpr(expr.value, type, expr.location)
  }

  override fun visitAccessExpr(expr: AccessExpr): TypedExpr {
    val variable = currentScope.findVariable(expr.name)
      ?: return violate("Unknown identifier %s", expr.name)

    return TypedAccessExpr(variable, expr.location)
  }

  override fun visitCallExpr(expr: CallExpr): TypedExpr {
    val callable = visit(expr.callee)

    val function = callable.type.cast<FunctionType>()
      ?: return violate("Can not call not function", callable)

    return function.call(visit(expr.arguments))
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

    return value
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

    return value
  }

  override fun visitGetExpr(expr: GetExpr): TypedExpr {
    val receiver = visit(expr.receiver)

    val struct = receiver.type.cast<StructType>()
      ?: return violate("Can not get a property from non-struct expression %s", receiver)

    val property = struct.property(expr.property)
      ?: return violate("Unknown property %s in struct %s", expr.property, struct)

    return property.value ?: undeclared(property.type)
  }

  override fun visitGroupExpr(group: Expr.Group): TypedExpr {
    val expr = visit(group.expr)

    return TypedGroupExpr(expr, group.location)
  }

  override fun visitInstanceExpr(expr: InstanceExpr): TypedExpr {
    val struct = visit(expr.struct).cast<StructType> {
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

    return int(32).const(type.size)
  }

  override fun visitRefExpr(expr: RefExpr): TypedExpr {
    val expr = visit(expr.expr)

    return TypedReferenceExpr(expr, expr.location)
  }

  override fun visitDerefExpr(value: Expr.Value): TypedExpr {
    val expr = visit(value.expr)

    val type = expr.type.cast<PointerType> {
      return violate("Can not deref from non-pointer type %s", it)
    }

    return type.const()
  }

  override fun visitErrorExpr(expr: ErrorExpr): TypedExpr {
    return TypedErrorExpr(expr.message, expr.arguments, expr.location)
  }

  override fun visitMatchExpr(expr: MatchExpr): TypedExpr {
    val subject = visit(expr.subject)

    val patterns = expr.patterns
      .entries
      .associate { (pattern, value) ->
        visit(pattern) to scoped(ClosureScope(Identifier.of("match"), currentScope)) {
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
    return TypedIdentPattern(pattern.name, untyped(), pattern.location)
  }

  override fun visitIfExpr(expr: IfExpr): TypedExpr {
    val cond = visit(expr.cond)

    if (cond.type != bool) {
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
    val expr = stmt.value?.let(::visit) ?: unit.const()

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
    val module = currentScope.findModule(decl.module)
      ?: return violate("Unresolved module %s", decl.module).stmt()

    return ResolvedImportDecl(module, decl.location)
  }

  override fun visitModuleDecl(decl: ModuleDecl): ResolvedStmt {
    val module = currentModuleTree.createModule(decl.name, currentScope, decl.content)
    val content = scoped(module.scope) {
      visit(decl.content).filterIsInstance<ResolvedDecl>()
    }

    return ResolvedModuleDecl(decl.name, content, decl.location)
  }

  override fun visitEnumDecl(decl: EnumDecl): ResolvedStmt {
    val name = decl.name

    val enum = delegate(pointer(enum(name))).also {
      currentScope.create(name, it)
    }

    val members = decl.members.associate { (name, parameters) ->
      val types = visit(parameters)

      currentScope.declare(name, function(enum, types))

      name to EnumMember(name, types)
    }

    enum.value = enum(name, members)

    return ResolvedEnumDecl(name, members, decl.location)
  }

  override fun visitStructDecl(decl: StructDecl): ResolvedStmt {
    val name = decl.name

    val struct = delegate(struct(decl.name)).also {
      currentScope.create(name, it)
    }

    val properties = decl.properties.associate { (mutable, name, type) ->
      name to StructProperty(mutable, name, visit(type))
    }

    struct.value = struct(name, properties)

    return ResolvedStructDecl(name, properties, decl.location)
  }

  override fun visitFunDecl(decl: FunDecl): ResolvedStmt {
    val name = decl.name
    val realParameters = decl.realParameters.mapValues { visit(it.value) }

    val location = decl.location

    val attributes = decl.modifiers
      .associate { "native" to Attribute.native }

    val returnType = visit(decl.returnType) { unit }
    val type = FunctionType(
      parameters = decl.parameters.map { visit(it) },
      returnType = returnType
    )

    currentScope.declare(name, type)

    val scope = FunctionScope(type, name, currentScope, currentModuleTree)
    val content = scoped(name, scope) {
      decl.realParameters
        .mapKeys { it.key }
        .forEach { (name, type) ->
          declare(name, visit(type))
        }

      visit(decl.body)
    }

    return ResolvedFunDecl(name, content, realParameters, attributes, type, location)
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
    return currentScope.findType(ref.identifier)
      ?: return violate("Unresolved type reference to %s", ref.identifier).type
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): PlankType {
    return pointer(visit(ref.type))
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef): PlankType {
    return array(visit(ref.type))
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef): PlankType {
    val parameters = visit(ref.parameters)
    // TODO: infer if hasn't user-defined type
    val returnType = visit(ref.returnType) { unit }

    return FunctionType(parameters, returnType)
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

        val member = enum.member(pattern.type) ?: return run {
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

    return TypedConstExpr(Unit, untyped(), Location.undefined())
  }

  private fun undeclared(type: PlankType): TypedExpr {
    return TypedConstExpr(Unit, type, Location.undefined())
  }

  private val currentScope get() = scopes.peekLast()
  private val currentModuleTree get() = scopes.peekLast().moduleTree

  private fun findVariable(name: Identifier): Variable {
    return currentScope.findVariable(name)
      ?: Variable(false, name, violate("Unknown variable", name))
  }

  private inline fun <T> scoped(scope: Scope, body: Scope.() -> T): T {
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }

  private inline fun <T> scoped(
    name: Identifier = Identifier.of("anonymous"),
    scope: Scope = ClosureScope(name, scopes.peekLast()),
    body: Scope.() -> T
  ): T {
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }
}
