@file:Suppress("MaxLineLength", "MaximumLineLength")

package org.plank.analyzer.phases

import org.plank.analyzer.ArrayType
import org.plank.analyzer.AttributeScope
import org.plank.analyzer.BindingViolation
import org.plank.analyzer.BoolType
import org.plank.analyzer.CharType
import org.plank.analyzer.ClosureScope
import org.plank.analyzer.DelegateType
import org.plank.analyzer.EnumMember
import org.plank.analyzer.EnumType
import org.plank.analyzer.FileScope
import org.plank.analyzer.FloatType
import org.plank.analyzer.FunctionScope
import org.plank.analyzer.FunctionType
import org.plank.analyzer.GlobalScope
import org.plank.analyzer.IntType
import org.plank.analyzer.Module
import org.plank.analyzer.ModuleTree
import org.plank.analyzer.ModuleType
import org.plank.analyzer.PlankType
import org.plank.analyzer.PointerType
import org.plank.analyzer.Scope
import org.plank.analyzer.StructProperty
import org.plank.analyzer.StructType
import org.plank.analyzer.UnitType
import org.plank.analyzer.Untyped
import org.plank.analyzer.Variable
import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedDecl
import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.element.ResolvedErrorDecl
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.element.ResolvedUseDecl
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedErrorExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedNamedTuplePattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedViolatedPattern
import org.plank.analyzer.visit
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ArrayTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.ErrorDecl
import org.plank.syntax.element.ErrorExpr
import org.plank.syntax.element.ErrorStmt
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.FunctionTypeRef
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.GroupExpr
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.IfExpr
import org.plank.syntax.element.InstanceExpr
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.Location
import org.plank.syntax.element.MatchExpr
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.NamedTuplePattern
import org.plank.syntax.element.NoBody
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankElement
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.PointerTypeRef
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.ReturnStmt
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.TreeWalker
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UnitTypeRef
import org.plank.syntax.element.UseDecl
import pw.binom.Stack

// TODO: add call parameters check
@Suppress("UnusedPrivateMember")
class AnalyzingPhase(tree: ModuleTree) :
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<ResolvedStmt>,
  Pattern.Visitor<TypedPattern>,
  PlankFile.Visitor<ResolvedPlankFile>,
  FunctionBody.Visitor<ResolvedFunctionBody>,
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
      .map(this::visitPlankFile)
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
          override fun visitUseDecl(decl: UseDecl) {
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

  override fun visitBlockExpr(expr: BlockExpr): TypedExpr {
    return scoped {
      val returned = expr.returned?.let(::visitExpr) ?: UnitType.const()
      val stmts = visitStmts(expr.stmts)

      TypedBlockExpr(stmts, returned, references, returned.type, expr.location)
    }
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
      else -> return expr.violate("Unresolved type ${expr.value::class.simpleName}")
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
      currentScope.references[variable.name] = variable.value.type
    }

    return TypedAccessExpr(null, variable, expr.location)
  }

  override fun visitCallExpr(expr: CallExpr): TypedExpr {
    val callable = visitExpr(expr.callee)

    val function = callable.type.cast<FunctionType>()
      ?: return callable.violate("Type ${callable.type} is not callable")

    val arguments = visitExprs(expr.arguments)

    return function.call(callable, expr.location, arguments)
  }

  override fun visitAssignExpr(expr: AssignExpr): TypedExpr {
    val reference = findVariable(expr.name)
    val value = visitExpr(expr.value)

    if (!reference.mutable) {
      return expr.violate("Can not reassign immutable variable `${reference.name.text}`")
    }

    if (reference.value.type != value.type) {
      return value.violate("Mismatch types: expecting ${reference.value.type} but got ${value.type}")
    }

    reference.value = value

    return TypedAssignExpr(null, reference.name, value, value.type, expr.location)
  }

  @Suppress("ReturnCount")
  override fun visitSetExpr(expr: SetExpr): TypedExpr {
    val newValue = visitExpr(expr.value)

    val receiver = when (val receiver = expr.receiver) {
      is AccessExpr -> {
        val name = receiver.path.toIdentifier()
        val location = receiver.location

        val value = currentScope.findVariable(name) ?: currentScope.findModule(name)

        if (value is Module) {
          val type = ModuleType(value.name, value.scope.variables.values.toList())

          val property = type.variable(expr.property)
            ?: return expr.property.violate("Unresolved property `${expr.property.text}` in module `${type.name}`")

          if (!property.mutable) {
            return expr.violate("Can not reassign immutable property `${property.name.text}` of module `${type.name}`")
          }

          if (property.type != newValue.type) {
            return newValue.violate("Mismatch types: expecting ${property.type} but got ${newValue.type}")
          }

          return TypedAssignExpr(value, property.name, newValue, newValue.type, location)
        } else {
          visitExpr(receiver)
        }
      }
      else -> {
        visitExpr(receiver)
      }
    }

    val struct = receiver.type.cast<StructType>()
      ?: return receiver.violate("Can not set property `${expr.property.text}` from type ${receiver.type} because it is not a struct")

    val property = struct.property(expr.property)
      ?: return expr.property.violate("Unresolved property `${expr.property.text}` in struct $struct")

    if (!property.mutable) {
      return expr.violate("Can not reassign immutable property `${property.name.text}` of struct $struct")
    }

    if (property.type != newValue.type) {
      return newValue.violate("Mismatch types: expecting ${property.type} but got ${newValue.type}")
    }

    return TypedSetExpr(receiver, property.name, newValue, newValue.type, expr.location)
  }

  override fun visitGetExpr(expr: GetExpr): TypedExpr {
    val receiver = when (val receiver = expr.receiver) {
      is AccessExpr -> {
        val name = receiver.path.toIdentifier()
        val location = receiver.location

        val value = currentScope.findVariable(name) ?: currentScope.findModule(name)

        if (value is Module) {
          val type = ModuleType(value.name, value.scope.variables.values.toList())

          val variable = type.variable(expr.property)
            ?: return expr.property.violate("Unresolved property `${expr.property.text}` in module `${type.name}`")

          return TypedAccessExpr(value, variable, location)
        } else {
          visitExpr(receiver)
        }
      }
      else -> {
        visitExpr(receiver)
      }
    }

    val struct = receiver.type.cast<StructType>()
      ?: return receiver.violate("Can not get property `${expr.property.text}` from type ${receiver.type} because it is not a struct or a module")

    val property = struct.property(expr.property)
      ?: return expr.property.violate("Unresolved property `${expr.property.text}` in struct $struct")

    return TypedGetExpr(receiver, property.name, property.type, expr.location)
  }

  override fun visitGroupExpr(expr: GroupExpr): TypedExpr {
    val inner = visitExpr(expr.value)

    return TypedGroupExpr(inner, inner.location)
  }

  override fun visitInstanceExpr(expr: InstanceExpr): TypedExpr {
    val struct = visitTypeRef(expr.type).cast<StructType> {
      return expr.type.violate("Type $it can not be instantiated")
    }

    val arguments = expr.arguments.mapValues { (name, expr) ->
      val value = visitExpr(expr)
      val property = struct.property(name)
        ?: return name.violate("Unresolved property `${name.text}` in struct $struct")

      if (property.type != value.type) {
        return value.violate("Mismatch types: expecting ${property.type} but got ${value.type}")
      }

      value
    }

    return struct.instantiate(expr.location, arguments)
  }

  override fun visitSizeofExpr(expr: SizeofExpr): TypedExpr {
    val type = visitTypeRef(expr.type)

    return IntType(32).const(type.size)
  }

  override fun visitRefExpr(expr: RefExpr): TypedExpr {
    val inner = visitExpr(expr.value)

    return TypedRefExpr(inner, inner.location)
  }

  override fun visitDerefExpr(expr: DerefExpr): TypedExpr {
    val ref = visitExpr(expr.value)

    val type = ref.type.cast<PointerType> {
      return expr.violate("Type ${ref.type} can not be dereferenced")
    }

    return TypedDerefExpr(ref, type.inner, expr.location)
  }

  override fun visitErrorExpr(expr: ErrorExpr): TypedExpr {
    return TypedErrorExpr(expr.message, expr.arguments, expr.location)
  }

  override fun visitMatchExpr(expr: MatchExpr): TypedExpr {
    val subject = visitExpr(expr.subject)

    val patterns = expr.patterns
      .entries
      .associate { (pattern, value) ->
        scoped(ClosureScope(Identifier("match"), currentScope)) {
          deconstruct(pattern, subject)

          visitPattern(pattern) to visitExpr(value)
        }
      }

    val value = patterns.values.reduce { acc, next ->
      if (acc.type != next.type) {
        return next.violate("Mismatch types: expecting ${acc.type}, but got ${next.type}")
      }

      next
    }

    return TypedMatchExpr(subject, patterns, value.type, expr.location)
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): TypedPattern {
    val type = currentScope.findType(pattern.type.toIdentifier())
      ?: return pattern.type.violatedPattern("Unresolved type reference `${pattern.type.text}`")

    val enum = type.cast<StructType> {
      return pattern.type.violatedPattern("Type $type can not be destructured")
    }

    val properties = visitPatterns(pattern.properties)

    return TypedNamedTuplePattern(properties, enum, pattern.location)
  }

  override fun visitIdentPattern(pattern: IdentPattern): TypedPattern {
    val variable = findVariable(pattern.name)

    return TypedIdentPattern(pattern.name, variable.value.type, pattern.location)
  }

  override fun visitIfExpr(expr: IfExpr): TypedExpr {
    val cond = visitExpr(expr.cond)

    if (cond.type != BoolType) {
      return cond.violate("Mismatch types: expecting $BoolType, but got ${cond.type}")
    }

    val thenBranch = visitExpr(expr.thenBranch)
    val elseBranch = expr.elseBranch?.let { visitExpr(it) }

    if (elseBranch == null) {
      return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, expr.location)
    }

    if (thenBranch.type != elseBranch.type) {
      return expr.violate("Mismatch types: expecting if type ${thenBranch.type}, but got ${elseBranch.type}")
    }

    return TypedIfExpr(cond, thenBranch, elseBranch, thenBranch.type, expr.location)
  }

  override fun visitExprStmt(stmt: ExprStmt): ResolvedStmt {
    return ResolvedExprStmt(visitExpr(stmt.expr), stmt.location)
  }

  override fun visitReturnStmt(stmt: ReturnStmt): ResolvedStmt {
    val expr = stmt.value?.let(::visitExpr) ?: UnitType.const()

    val functionScope = currentScope as? FunctionScope
      ?: return stmt
        .violate("Can not return in not function scope `${currentScope.name.text}`")
        .stmt()

    if (functionScope.returnType != expr.type) {
      return stmt
        .violate("Mismatch types: expecting return type ${functionScope.returnType}, but got ${expr.type}")
        .stmt()
    }

    return ResolvedReturnStmt(expr, stmt.location)
  }

  override fun visitErrorStmt(stmt: ErrorStmt): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitUseDecl(decl: UseDecl): ResolvedStmt {
    val module = currentScope.findModule(decl.path.toIdentifier())
      ?: return decl.violate("Unresolved module `${decl.path.text}`").stmt()

    return ResolvedUseDecl(module, decl.location)
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

    val enum = DelegateType(EnumType(name)).also {
      currentScope.create(name, it)
    }

    val members = decl.members.associate { (name, parameters) ->
      val types = visitTypeRefs(parameters)
      val functionType = FunctionType(
        enum,
        types,
        types.withIndex().associate { (index, type) -> Identifier("_$index") to type },
      ).copy(actualReturnType = enum)

      currentScope.create(
        name,
        StructType(
          name,
          functionType.realParameters.mapValues { StructProperty(false, it.key, it.value) }
        )
      )

      if (types.isEmpty()) {
        currentScope.declare(name, enum)
      } else {
        currentScope.declare(name, functionType)
      }

      name to EnumMember(name, types, functionType)
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
      name to StructProperty(mutable, name, visitTypeRef(type))
    }

    struct.value = StructType(name, properties)

    return ResolvedStructDecl(name, properties, struct, decl.location)
  }

  override fun visitFunDecl(decl: FunDecl): ResolvedStmt {
    val name = decl.name
    val realParameters = decl.realParameters.mapValues { closureIfFunction(visitTypeRef(it.value)) }

    val location = decl.location

    val attributes = decl.attributes // todo validate

    val references = LinkedHashMap<Identifier, PlankType>()
    val type = visitFunctionTypeRef(decl.type).copy(references = references)

    currentScope.declare(name, type, decl.location)

    val scope = FunctionScope(type, name, currentScope, currentModuleTree, references)
    val content = scoped(name, scope) {
      decl.realParameters
        .mapKeys { it.key }
        .forEach { (name, type) ->
          declare(name, closureIfFunction(visitTypeRef(type)))
        }

      visitFunctionBody(decl.body)
    }

    return ResolvedFunDecl(name, content, realParameters, attributes, references, type, location)
  }

  override fun visitLetDecl(decl: LetDecl): ResolvedStmt {
    val name = decl.name
    val mutable = decl.mutable
    val value = visitExpr(decl.value)
    val isNested = !currentScope.isTopLevelScope

    currentScope.declare(name, value, mutable)

    // TODO: use explicit type to infer with [bindings]
    return ResolvedLetDecl(name, mutable, value, isNested, value.type, decl.location)
  }

  override fun visitErrorDecl(decl: ErrorDecl): ResolvedStmt {
    return ResolvedErrorDecl(decl.message, decl.arguments, decl.location)
  }

  override fun visitNoBody(body: NoBody): ResolvedFunctionBody {
    return ResolvedNoBody(body.location)
  }

  override fun visitExprBody(body: ExprBody): ResolvedFunctionBody {
    return ResolvedExprBody(visitExpr(body.expr), body.location)
  }

  override fun visitCodeBody(body: CodeBody): ResolvedFunctionBody {
    return ResolvedCodeBody(visitStmts(body.stmts), body.returned?.let(::visitExpr), body.location)
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): PlankType {
    return currentScope.findType(ref.path.toIdentifier())
      ?: return ref.violate("Unresolved type reference `${ref.path.text}`").type
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): PlankType {
    return PointerType(visitTypeRef(ref.type))
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef): PlankType {
    return ArrayType(visitTypeRef(ref.type))
  }

  // TODO: infer if hasn't user-defined type
  override fun visitFunctionTypeRef(ref: FunctionTypeRef): FunctionType {
    val parameter = visit(ref.parameter) { Untyped }
    val returnType = visit(ref.returnType) { UnitType }

    val actualReturnType = visit(ref.actualReturnType) { UnitType }
    val realParameters = ref.realParameters.mapValues { visitTypeRef(it.value) }

    return FunctionType(
      parameter,
      returnType,
      actualReturnType,
      realParameters,
      isNested = if (ref.isClosure == null) !currentScope.isTopLevelScope else ref.isClosure!!
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
          subject.violate("Expecting a enum type with named tuple pattern, but got ${subject.type}")
        }

        val member = enum.member(pattern.type.toIdentifier()) ?: return run {
          pattern.type.violate("Unresolved enum variant `${pattern.type.text}` of `${name.text}`")
        }

        pattern.properties.forEachIndexed { index, subPattern ->
          val subType = member.fields.getOrNull(index) ?: return run {
            subPattern.violatedPattern("Expecting ${member.fields.size} fields when matching `${member.name.text}`, but got $index fields instead")
          }

          deconstruct(subPattern, undeclared(subType), enum)
        }
      }
    }
  }

  private fun PlankElement.violatedPattern(message: String): TypedPattern {
    violations = violations + BindingViolation(message, location)

    return TypedViolatedPattern(message, location = location)
  }

  private fun PlankElement.violate(message: String): TypedExpr {
    violations = violations + BindingViolation(message, location)

    return TypedConstExpr(Unit, Untyped, location)
  }

  private fun undeclared(type: PlankType): TypedExpr {
    return TypedConstExpr(Unit, type, Location.Generated)
  }

  private val attributeScope = AttributeScope(tree)

  private val currentScope get() = scopes.peekLast()
  private val currentModuleTree get() = scopes.peekLast().moduleTree

  private fun closureIfFunction(type: PlankType): PlankType {
    return when (type) {
      is FunctionType -> type.copy(isNested = true)
      else -> type
    }
  }

  private fun findVariable(name: Identifier): Variable {
    return currentScope.findVariable(name)
      ?: Variable(false, name, name.violate("Unresolved variable `${name.text}`"), currentScope)
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
