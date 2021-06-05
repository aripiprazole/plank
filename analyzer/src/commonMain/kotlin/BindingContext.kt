package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.analyzer.PlankType.Companion.array
import com.lorenzoog.plank.analyzer.PlankType.Companion.bool
import com.lorenzoog.plank.analyzer.PlankType.Companion.char
import com.lorenzoog.plank.analyzer.PlankType.Companion.delegate
import com.lorenzoog.plank.analyzer.PlankType.Companion.enum
import com.lorenzoog.plank.analyzer.PlankType.Companion.float
import com.lorenzoog.plank.analyzer.PlankType.Companion.function
import com.lorenzoog.plank.analyzer.PlankType.Companion.int
import com.lorenzoog.plank.analyzer.PlankType.Companion.pointer
import com.lorenzoog.plank.analyzer.PlankType.Companion.struct
import com.lorenzoog.plank.analyzer.PlankType.Companion.unit
import com.lorenzoog.plank.analyzer.PlankType.Companion.untyped
import com.lorenzoog.plank.analyzer.element.TypedAccessExpr
import com.lorenzoog.plank.analyzer.element.TypedConstExpr
import com.lorenzoog.plank.analyzer.element.TypedDecl
import com.lorenzoog.plank.analyzer.element.TypedEnumDecl
import com.lorenzoog.plank.analyzer.element.TypedExpr
import com.lorenzoog.plank.analyzer.element.TypedExprStmt
import com.lorenzoog.plank.analyzer.element.TypedFunDecl
import com.lorenzoog.plank.analyzer.element.TypedGroupExpr
import com.lorenzoog.plank.analyzer.element.TypedIdentPattern
import com.lorenzoog.plank.analyzer.element.TypedIfExpr
import com.lorenzoog.plank.analyzer.element.TypedImportDecl
import com.lorenzoog.plank.analyzer.element.TypedLetDecl
import com.lorenzoog.plank.analyzer.element.TypedMatchExpr
import com.lorenzoog.plank.analyzer.element.TypedModuleDecl
import com.lorenzoog.plank.analyzer.element.TypedNamedTuplePattern
import com.lorenzoog.plank.analyzer.element.TypedPattern
import com.lorenzoog.plank.analyzer.element.TypedPlankFile
import com.lorenzoog.plank.analyzer.element.TypedReferenceExpr
import com.lorenzoog.plank.analyzer.element.TypedReturnStmt
import com.lorenzoog.plank.analyzer.element.TypedStmt
import com.lorenzoog.plank.analyzer.element.TypedStructDecl
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeReference
import com.lorenzoog.plank.grammar.element.visit
import com.lorenzoog.plank.grammar.tree.TreeWalker
import com.lorenzoog.plank.shared.depthFirstSearch
import pw.binom.Stack

/**
 * This is the type-system core.
 *
 * This will map the file provided in [analyze] with the provided tree
 * into a typed one [TypedPlankFile] with typed declarations/statements/expressions.
 */
class BindingContext(tree: ModuleTree) :
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<TypedStmt>,
  Pattern.Visitor<TypedPattern>,
  PlankFile.Visitor<TypedPlankFile>,
  TypeReference.Visitor<PlankType> {
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

  var violations = emptySet<BindingViolation>()
    private set

  val isValid get() = violations.isEmpty()

  fun analyze(file: PlankFile): TypedPlankFile {
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
          override fun visitImportDecl(importDecl: Decl.ImportDecl) {
            addEdge(name, importDecl.module)
          }
        }

        dependencyTreeWalker.walk(this@searchDependencies)
      }
      .depthFirstSearch(name)
      .mapNotNull(currentModuleTree::findModule)
  }

  override fun visitPlankFile(file: PlankFile): TypedPlankFile {
    val program = visit(file.program).filterIsInstance<TypedDecl>()

    return TypedPlankFile(file, program)
  }

  override fun visitConstExpr(const: Expr.Const): TypedExpr {
    val type = when (const.value) {
      is Boolean -> bool
      is Unit -> bool
      is String -> pointer(char)
      is Int -> int(32)
      is Short -> int(16)
      is Byte -> int(8)
      is Double -> float(32)
      is Long, Float -> float(64)
      else -> return violate("Unknown type %s", const::class.simpleName)
    }

    return TypedConstExpr(const.value, type, const.location)
  }

  override fun visitAccessExpr(access: Expr.Access): TypedExpr {
    val variable = currentScope.findVariable(access.name)
      ?: return violate("Unknown identifier %s", access.name)

    return TypedAccessExpr(variable, access.location)
  }

  override fun visitCallExpr(call: Expr.Call): TypedExpr {
    val callable = visit(call.callee)

    val function = callable.type.cast<FunctionType>()
      ?: return violate("Can not call not function", callable)

    return function.call(visit(call.arguments))
  }

  override fun visitAssignExpr(assign: Expr.Assign): TypedExpr {
    val reference = findVariable(assign.name)
    val value = visit(assign.value)

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

  override fun visitSetExpr(set: Expr.Set): TypedExpr {
    val receiver = visit(set.receiver)
    val value = visit(set.value)

    val struct = receiver.type.cast<StructType>()
      ?: return violate("Can not update a property from non-struct expression %s", receiver)

    val property = struct.property(set.property)
      ?: return violate("Unknown property %s in struct %s", set.property, struct)

    if (property.type != value.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in set expression", property.type, value.type
      )
    }

    return value
  }

  override fun visitGetExpr(get: Expr.Get): TypedExpr {
    val receiver = visit(get.receiver)

    val struct = receiver.type.cast<StructType>()
      ?: return violate("Can not get a property from non-struct expression %s", receiver)

    val property = struct.property(get.property)
      ?: return violate("Unknown property %s in struct %s", get.property, struct)

    return property.value ?: undeclared(property.type)
  }

  override fun visitGroupExpr(group: Expr.Group): TypedExpr {
    val expr = visit(group.expr)

    return TypedGroupExpr(expr, group.location)
  }

  override fun visitInstanceExpr(instance: Expr.Instance): TypedExpr {
    val struct = visit(instance.struct).cast<StructType> {
      return violate("Can not get a instance from non-struct type %s", it)
    }

    val arguments = instance.arguments.mapValues { (name, expr) ->
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

    return struct.instantiate(instance.location, arguments)
  }

  override fun visitSizeofExpr(sizeof: Expr.Sizeof): TypedExpr {
    val type = visit(sizeof.type)

    return int(32).const(type.size)
  }

  override fun visitReferenceExpr(reference: Expr.Reference): TypedExpr {
    val expr = visit(reference.expr)

    return TypedReferenceExpr(expr, reference.location)
  }

  override fun visitValueExpr(value: Expr.Value): TypedExpr {
    val expr = visit(value.expr)

    val type = expr.type.cast<PointerType> {
      return violate("Can not deref from non-pointer type %s", it)
    }

    return type.const()
  }

  override fun visitMatchExpr(match: Expr.Match): TypedExpr {
    val subject = visit(match.subject)

    val patterns = match.patterns
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

    return TypedMatchExpr(subject, patterns, type, match.location)
  }

  override fun visitNamedTuplePattern(pattern: Pattern.NamedTuple): TypedPattern {
    TODO()
  }

  override fun visitIdentPattern(pattern: Pattern.Ident): TypedPattern {
    return TypedIdentPattern(pattern.name, untyped(), pattern.location)
  }

  override fun visitIfExpr(anIf: Expr.If): TypedExpr {
    val cond = visit(anIf.cond)

    if (cond.type != bool) {
      return violate("Mismatch types, expecting bool value but got %s in if condition", cond)
    }

    val thenBranch = visit(anIf.thenBranch)
    val elseBranch = anIf.elseBranch?.let { visit(it) }

    if (elseBranch == null) {
      return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, anIf.location)
    }

    if (thenBranch.type != elseBranch.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in if expression",
        thenBranch.type,
        elseBranch.type
      )
    }

    return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, anIf.location)
  }

  override fun visitExprStmt(exprStmt: Stmt.ExprStmt): TypedStmt {
    return TypedExprStmt(visit(exprStmt.expr), exprStmt.location)
  }

  override fun visitReturnStmt(returnStmt: Stmt.ReturnStmt): TypedStmt {
    val expr = returnStmt.value?.let(::visit) ?: unit.const()

    val functionScope = currentScope as? FunctionScope
      ?: return violate("Can not return in not function scope %s", currentScope).stmt()

    if (functionScope.returnType != expr.type) {
      return violate(
        "Mismatch types, expecting %s but got %s in return statement",
        functionScope.returnType, expr.type
      ).stmt()
    }

    return TypedReturnStmt(expr, returnStmt.location)
  }

  override fun visitImportDecl(importDecl: Decl.ImportDecl): TypedStmt {
    val module = currentScope.findModule(importDecl.module)
      ?: return violate("Unresolved module %s", importDecl.module).stmt()

    return TypedImportDecl(module, importDecl.location)
  }

  override fun visitModuleDecl(moduleDecl: Decl.ModuleDecl): TypedStmt {
    val module = currentModuleTree.createModule(moduleDecl.name, currentScope, moduleDecl.content)
    val content = scoped(module.scope) {
      visit(moduleDecl.content).filterIsInstance<TypedDecl>()
    }

    return TypedModuleDecl(moduleDecl.name, content, moduleDecl.location)
  }

  override fun visitEnumDecl(enumDecl: Decl.EnumDecl): TypedStmt {
    val name = enumDecl.name

    val enum = delegate(pointer(enum(name))).also {
      currentScope.create(name, it)
    }

    val members = enumDecl.members.associate { (name, parameters) ->
      val types = visit(parameters)

      currentScope.declare(name, function(enum, types))

      name to EnumMember(name, types)
    }

    enum.value = enum(name, members)

    return TypedEnumDecl(name, members, enumDecl.location)
  }

  override fun visitStructDecl(structDecl: Decl.StructDecl): TypedStmt {
    val name = structDecl.name

    val struct = delegate(struct(structDecl.name)).also {
      currentScope.create(name, it)
    }

    val properties = structDecl.properties.associate { (mutable, name, type) ->
      name to StructProperty(mutable, name, visit(type))
    }

    struct.value = struct(name, properties)

    return TypedStructDecl(name, properties, structDecl.location)
  }

  override fun visitFunDecl(funDecl: Decl.FunDecl): TypedStmt {
    val name = funDecl.name
    val realParameters = funDecl.realParameters.mapValues { visit(it.value) }

    val location = funDecl.location

    val attributes = funDecl.modifiers
      .associate { "native" to Attribute.native }

    val returnType = visit(funDecl.returnType) { unit }
    val type = FunctionType(
      parameters = funDecl.parameters.map { visit(it) },
      returnType = returnType
    )

    currentScope.declare(name, type)

    val scope = FunctionScope(type, name, currentScope, currentModuleTree)
    val content = scoped(name, scope) {
      funDecl.realParameters
        .mapKeys { it.key }
        .forEach { (name, type) ->
          declare(name, visit(type))
        }

      visit(funDecl.body)
    }

    return TypedFunDecl(name, content, realParameters, attributes, type, location)
  }

  override fun visitLetDecl(letDecl: Decl.LetDecl): TypedStmt {
    val name = letDecl.name
    val mutable = letDecl.mutable
    val value = visit(letDecl.value)

    currentScope.declare(name, value, mutable)

    // TODO: use explicit type to infer with [bindings]
    return TypedLetDecl(name, mutable, value, value.type, letDecl.location)
  }

  override fun visitAccessTypeReference(reference: TypeReference.Access): PlankType {
    return currentScope.findType(reference.identifier)
      ?: return violate("Unresolved type reference to %s", reference.identifier).type
  }

  override fun visitPointerTypeReference(reference: TypeReference.Pointer): PlankType {
    return pointer(visit(reference.type))
  }

  override fun visitArrayTypeReference(reference: TypeReference.Array): PlankType {
    return array(visit(reference.type))
  }

  override fun visitFunctionTypeReference(reference: TypeReference.Function): PlankType {
    val parameters = visit(reference.parameters)
    // TODO: infer if hasn't user-defined type
    val returnType = visit(reference.returnType) { unit }

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
      is Pattern.Ident -> {
        type.cast<EnumType>()?.member(pattern.name) ?: return declare(pattern.name, subject)
      }
      is Pattern.NamedTuple -> {
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
            violate("Unknown property %d of member %s", index, member.type)
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
