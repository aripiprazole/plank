@file:Suppress("MaxLineLength", "MaximumLineLength")

package org.plank.analyzer.infer

import org.plank.analyzer.AnalyzerViolation
import org.plank.analyzer.CanNotReassignImmutableStructMember
import org.plank.analyzer.CanNotReassignImmutableVariable
import org.plank.analyzer.IncorrectArity
import org.plank.analyzer.IncorrectEnumArity
import org.plank.analyzer.Redeclaration
import org.plank.analyzer.ScopeIsNotReturnable
import org.plank.analyzer.TypeInfoCanNotBeDestructured
import org.plank.analyzer.TypeIsInfinite
import org.plank.analyzer.TypeIsNotCallable
import org.plank.analyzer.TypeIsNotPointer
import org.plank.analyzer.TypeIsNotStruct
import org.plank.analyzer.TypeIsNotStructAndCanNotGet
import org.plank.analyzer.TypeMismatch
import org.plank.analyzer.UnresolvedEnumVariant
import org.plank.analyzer.UnresolvedModule
import org.plank.analyzer.UnresolvedStructMember
import org.plank.analyzer.UnresolvedType
import org.plank.analyzer.UnresolvedTypeAccess
import org.plank.analyzer.UnresolvedVariable
import org.plank.analyzer.UnsupportedConstType
import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedDecl
import org.plank.analyzer.element.ResolvedEnumDecl
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
import org.plank.analyzer.element.TypedBlockBranch
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedIfBranch
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedNamedTuplePattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.analyzer.element.TypedThenBranch
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ApplyTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.EnumDecl
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
import org.plank.syntax.element.IfBranch
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
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.element.TreeWalker
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UnitTypeRef
import org.plank.syntax.element.UseDecl
import org.plank.syntax.element.toIdentifier
import pw.binom.Stack

@Suppress("UnusedPrivateMember")
class Infer(tree: ModuleTree) :
  PlankFile.Visitor<ResolvedPlankFile>,
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<ResolvedStmt>,
  Pattern.Visitor<TypedPattern>,
  FunctionBody.Visitor<ResolvedFunctionBody>,
  IfBranch.Visitor<TypedIfBranch>,
  TypeRef.Visitor<Ty> {
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
        dependencies.last().copy(dependencies = dependencies.take(dependencies.size - 1))
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

    return ResolvedPlankFile(file, program, analyzerViolations = violations.toList())
  }

  override fun visitBlockExpr(expr: BlockExpr): TypedExpr {
    return scoped {
      val stmts = visitStmts(expr.stmts)
      val value = expr.value?.let(::visitExpr) ?: unitValue()

      TypedBlockExpr(stmts, value, references, expr.location)
    }
  }

  override fun visitMatchExpr(expr: MatchExpr): TypedExpr {
    val subject = visitExpr(expr.subject)

    val patterns = expr.patterns
      .entries
      .associate { (pattern, value) ->
        scoped(ClosureScope(Identifier("match"), currentScope)) {
          deconstruct(
            pattern,
            subject,
            findTyInfo(subject.ty) ?: return pattern.violate(UnresolvedType(subject.ty)),
          )

          visitPattern(pattern) to visitExpr(value)
        }
      }

    if (patterns.isEmpty()) {
      return TypedMatchExpr(subject, patterns, unitTy, Subst(), expr.location)
    }

    val fst = patterns.values.first()
    var subst: Subst = fst.subst

    val ty = patterns.values.drop(1).fold(fst.ty) { acc, next ->
      subst = subst compose unify(acc, next.ty)

      val ty = acc ap subst

      if (ty != next.ty ap subst) {
        return next.violate(TypeMismatch(acc, next.ty))
      }

      ty
    }

    return TypedMatchExpr(subject, patterns, ty, subst, expr.location)
  }

  override fun visitIfExpr(expr: IfExpr): TypedExpr {
    val cond = visitExpr(expr.cond)

    if (boolTy != cond.ty) {
      return cond.violate(TypeMismatch(boolTy, cond.ty))
    }

    val thenBranch = visitIfBranch(expr.thenBranch)
    val elseBranch = expr.elseBranch?.let { visitIfBranch(it) }
      ?: return TypedIfExpr(cond, thenBranch, null, thenBranch.ty, thenBranch.subst, expr.location)

    val subst = thenBranch.subst compose elseBranch.subst
    val ty = thenBranch.ty ap subst

    if (ty != elseBranch.ty) {
      return elseBranch.violate(TypeMismatch(ty, elseBranch.ty))
    }

    return TypedIfExpr(cond, thenBranch, elseBranch, ty, subst, expr.location)
  }

  override fun visitConstExpr(expr: ConstExpr): TypedExpr {
    val type = when (val value = expr.value) {
      is Boolean -> boolTy
      is Unit -> unitTy
      is Int -> i32Ty
      is Short -> i16Ty
      is Byte -> i8Ty
      is Double -> doubleTy
      is Float -> floatTy
      is String -> PtrTy(charTy)
      else -> return expr.violate(UnsupportedConstType(value::class))
    }

    return TypedConstExpr(expr.value, type, Subst(), expr.location)
  }

  override fun visitAccessExpr(expr: AccessExpr): TypedExpr {
    val variable = findVariable(expr.path.toIdentifier())

    if (
      !variable.isInScope &&
      variable.declaredIn !is FileScope &&
      variable.declaredIn !is GlobalScope
    ) {
      currentScope.references[variable.name] = variable.ty
    }

    return TypedAccessExpr(null, variable, expr.location)
  }

  override fun visitCallExpr(expr: CallExpr): TypedExpr {
    val callee = visitExpr(expr.callee)

    val ty = callee.ty as? FunTy ?: return callee.violate(TypeIsNotCallable(callee.ty))
    val parameters = ty.chainParameters()

    if (callee is TypedAccessExpr && callee.variable is InlineVariable && parameters.size == expr.arguments.size) {
      val variable = callee.variable
      val arguments = visitExprs(expr.arguments)

      arguments.zip(parameters).forEach { (arg, param) ->
        if (param != arg.ty) {
          arg.violate(TypeMismatch(param, arg.ty))
        }
      }

      return when (val body = variable.inlineCall(arguments)) {
        is ResolvedCodeBody -> TypedBlockExpr(body.stmts, body.value!!, location = expr.location)
        is ResolvedExprBody -> body.expr
        is ResolvedNoBody -> unitValue()
      }
    }

    return visitExprs(expr.arguments)
      .ifEmpty { listOf(unitValue()) }
      .foldIndexed(callee) { i, acc, argument ->
        val parameter = parameters.elementAtOrNull(i)
        var parameterTy = parameter ?: argument.ty
        var argumentTy = argument.ty
        var nestTy = ty.nest(i)
        var subst = Subst()

        if (parameter == null) {
          argument.violate(IncorrectArity(parameters.size, i))
        } else {
          subst = unify(argument.ty, parameter)
          parameterTy = parameter ap subst
          argumentTy = argumentTy ap subst
          nestTy = nestTy ap subst
        }

        if (parameterTy != argumentTy) {
          argument.violate(TypeMismatch(parameterTy, argumentTy))
        }

        TypedCallExpr(acc, argument, nestTy, subst, expr.location)
      }
  }

  override fun visitAssignExpr(expr: AssignExpr): TypedExpr {
    val variable = findVariable(expr.name)
    val value = visitExpr(expr.value)

    if (!variable.mutable) {
      return expr.violate(CanNotReassignImmutableVariable(variable.name))
    }

    if (variable.ty != value.ty) {
      return value.violate(TypeMismatch(variable.ty, value.ty))
    }

    return TypedAssignExpr(null, variable.name, value, value.ty, Subst(), expr.location)
  }

  override fun visitSetExpr(expr: SetExpr): TypedExpr {
    val newValue = visitExpr(expr.value)

    val receiver = when (val receiver = expr.receiver) {
      is AccessExpr -> {
        val name = receiver.path.toIdentifier()

        when (val value = currentScope.findVariable(name) ?: currentScope.findModule(name)) {
          is Module -> {
            val variable = value.scope.findVariable(expr.property)
              ?: return expr.property.violate(UnresolvedVariable(expr.property, value))

            return TypedAccessExpr(value, variable, receiver.location)
          }
          else -> visitExpr(receiver)
        }
      }
      else -> visitExpr(receiver)
    }

    val info = findTyInfo(receiver.ty)
      ?: return receiver.violate(UnresolvedType(receiver.ty))

    val struct = info.getAs<StructInfo>()
      ?: return receiver.violate(TypeIsNotStructAndCanNotGet(expr.property, receiver.ty))

    val property = struct.members[expr.property]
      ?: return expr.property.violate(UnresolvedStructMember(expr.property, struct))

    if (!property.mutable) {
      return expr.violate(CanNotReassignImmutableStructMember(property.name, struct))
    }

    if (property.ty != newValue.ty) {
      return newValue.violate(TypeMismatch(property.ty, newValue.ty))
    }

    return TypedSetExpr(
      receiver,
      property.name,
      newValue,
      struct,
      property.ty,
      Subst(),
      expr.location
    )
  }

  override fun visitGetExpr(expr: GetExpr): TypedExpr {
    val receiver = when (val receiver = expr.receiver) {
      is AccessExpr -> {
        val name = receiver.path.toIdentifier()

        when (val value = currentScope.findVariable(name) ?: currentScope.findModule(name)) {
          is Module -> {
            val variable = value.scope.findVariable(expr.property)
              ?: return expr.property.violate(UnresolvedVariable(expr.property, value))

            return TypedAccessExpr(value, variable, receiver.location)
          }
          else -> visitExpr(receiver)
        }
      }
      else -> visitExpr(receiver)
    }

    val info = findTyInfo(receiver.ty) ?: return receiver.violate(UnresolvedType(receiver.ty))

    val struct = info.getAs<StructInfo>()
      ?: return receiver.violate(TypeIsNotStructAndCanNotGet(expr.property, receiver.ty))

    val property = struct.members[expr.property]
      ?: return expr.property.violate(UnresolvedStructMember(expr.property, struct))

    return TypedGetExpr(receiver, property.name, struct, property.ty, Subst(), expr.location)
  }

  override fun visitGroupExpr(expr: GroupExpr): TypedExpr {
    return TypedGroupExpr(visitExpr(expr.value), Subst(), expr.location)
  }

  override fun visitInstanceExpr(expr: InstanceExpr): TypedExpr {
    val basicTy = visitTypeRef(expr.type)

    val info = findTyInfo(basicTy) ?: return expr.type.violate(UnresolvedType(basicTy))
    val struct = info.getAs<StructInfo>() ?: return expr.type.violate(TypeIsNotStruct(basicTy))

    var subst = Subst()
    val arguments = expr.arguments.mapValues { (name, expr) ->
      val value = visitExpr(expr)
      val property = struct.members[name]
        ?: return name.violate(UnresolvedStructMember(name, struct))

      subst = subst compose unify(property.ty, value.ty)

      val propertyTy = property.ty ap subst
      if (propertyTy != value.ty) {
        return value.violate(TypeMismatch(propertyTy, value.ty))
      }

      value
    }

    val ty = inst(
      Scheme(
        struct.names.map { it.text }.toSet(),
        struct.names.fold(ConstTy(struct.name.text) as Ty) { acc, next ->
          AppTy(acc, VarTy(next.text))
        }
      ),
    )

    return TypedInstanceExpr(arguments, struct, ty ap subst, Subst(), expr.location)
  }

  override fun visitSizeofExpr(expr: SizeofExpr): TypedExpr {
    return TypedSizeofExpr(visitTypeRef(expr.type), Subst(), expr.location)
  }

  override fun visitRefExpr(expr: RefExpr): TypedExpr {
    return TypedRefExpr(visitExpr(expr.value), Subst(), expr.location)
  }

  override fun visitDerefExpr(expr: DerefExpr): TypedExpr {
    val value = visitExpr(expr.value)

    val ty = value.ty as? PtrTy ?: return expr.value.violate(TypeIsNotPointer(value.ty))

    return TypedDerefExpr(value, ty.arg, Subst(), expr.location)
  }

  override fun visitNoBody(body: NoBody): ResolvedFunctionBody {
    return ResolvedNoBody(body.location)
  }

  override fun visitExprBody(body: ExprBody): ResolvedFunctionBody {
    return ResolvedExprBody(visitExpr(body.expr), body.location)
  }

  override fun visitCodeBody(body: CodeBody): ResolvedFunctionBody {
    return ResolvedCodeBody(visitStmts(body.stmts), body.value?.let(::visitExpr), body.location)
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): TypedPattern {
    val info = currentScope.findTyInfo(pattern.type.toIdentifier())
      ?: return pattern.type.violatedPattern(UnresolvedTypeAccess(pattern.type.toIdentifier()))

    val enum = info.getAs<EnumMemberInfo>()
      ?: return pattern.type.violatedPattern(TypeInfoCanNotBeDestructured(info))

    val properties = visitPatterns(pattern.properties)

    return TypedNamedTuplePattern(
      properties,
      enum,
      enum.ty,
      Subst(),
      pattern.location
    )
  }

  override fun visitIdentPattern(pattern: IdentPattern): TypedPattern {
    val variable = findVariable(pattern.name)

    return TypedIdentPattern(pattern.name, variable.ty, Subst(), pattern.location)
  }

  override fun visitThenBranch(branch: ThenBranch): TypedIfBranch {
    return TypedThenBranch(visitExpr(branch.value), branch.location)
  }

  override fun visitBlockBranch(branch: BlockBranch): TypedIfBranch {
    return scoped {
      val stmts = visitStmts(branch.stmts)
      val value = branch.value?.let(::visitExpr) ?: unitValue()

      TypedBlockBranch(stmts, value, references, branch.location)
    }
  }

  override fun visitExprStmt(stmt: ExprStmt): ResolvedStmt {
    return ResolvedExprStmt(visitExpr(stmt.expr), stmt.location)
  }

  override fun visitReturnStmt(stmt: ReturnStmt): ResolvedStmt {
    val value = stmt.value?.let(::visitExpr) ?: unitValue()

    val functionScope = currentScope as? FunctionScope
      ?: return stmt.violate(ScopeIsNotReturnable(currentScope)).stmt()

    if (functionScope.function.returnTy != value.ty) {
      return stmt.violate(TypeMismatch(functionScope.function.returnTy, value.ty)).stmt()
    }

    return ResolvedReturnStmt(value, stmt.location)
  }

  override fun visitUseDecl(decl: UseDecl): ResolvedStmt {
    val module = currentScope.findModule(decl.path.toIdentifier())
      ?: return decl.violate(UnresolvedModule(decl.path.toIdentifier())).stmt()

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
    val ty = inst(
      Scheme(
        decl.names.map { it.text }.toSet(),
        decl.names.fold(ConstTy(decl.name.text) as Ty) { acc, next ->
          AppTy(acc, VarTy(next.text))
        }
      ),
    )

    if (currentScope.findTyInfo(decl.name) != null) {
      return decl.name.violate(Redeclaration(decl.name)).stmt()
    }

    currentScope.create(EnumInfo(decl.name, ty))

    val members = decl.members.associate { (name, parameters) ->
      val types = visitTypeRefs(parameters)

      val memberInfo = currentScope.create(EnumMemberInfo(name, ty, types, FunTy(ty, types)))

      if (types.isEmpty()) {
        currentScope.declare(name, ty)
      } else {
        currentScope.declare(name, FunTy(ty, types))
      }

      name to memberInfo
    }

    val info = currentScope.create(EnumInfo(decl.name, ty, decl.names, members))

    return ResolvedEnumDecl(info, ty, decl.location)
  }

  override fun visitStructDecl(decl: StructDecl): ResolvedStmt {
    val ty = inst(
      Scheme(
        decl.names.map { it.text }.toSet(),
        decl.names.fold(ConstTy(decl.name.text) as Ty) { acc, next ->
          AppTy(acc, VarTy(next.text))
        }
      ),
    )

    if (currentScope.findTyInfo(decl.name) != null) {
      return decl.name.violate(Redeclaration(decl.name)).stmt()
    }

    currentScope.create(StructInfo(decl.name, ty))

    val properties = decl.properties.associate { (mutable, name, type) ->
      name to StructMemberInfo(name, visitTypeRef(type), mutable)
    }

    val info = currentScope.create(StructInfo(decl.name, ty, decl.names, properties))

    return ResolvedStructDecl(info, ty, decl.location)
  }

  override fun visitFunDecl(decl: FunDecl): ResolvedStmt {
    val name = decl.name
    val attributes = decl.attributes // todo validate

    val parameters = decl.parameters
      .mapValues { visitTypeRef(it.value) }
      .ifEmpty { mapOf(Identifier("_") to unitTy) }

    val returnType = visitTypeRef(decl.returnType)

    val ty = FunTy(returnType, parameters.values)
    val info = FunctionInfo(name, ty, returnType, parameters)

    val isNested = !currentScope.isTopLevelScope
    val references = linkedMapOf<Identifier, Ty>()

    if (currentScope.findVariable(name) != null) {
      return name.violate(Redeclaration(name)).stmt()
    }

    currentScope.declare(name, ty)

    val scope = FunctionScope(info, name, currentScope, currentModuleTree, references)
    val body = scoped(name, scope) {
      decl.parameters
        .mapKeys { it.key }
        .forEach { (name, type) ->
          declare(name, visitTypeRef(type))
        }

      visitFunctionBody(decl.body)
    }

    return ResolvedFunDecl(body, attributes, references, info, isNested, ty, Subst(), decl.location)
  }

  override fun visitLetDecl(decl: LetDecl): ResolvedStmt {
    val name = decl.name
    val mutable = decl.mutable
    val value = visitExpr(decl.value)
    val ty = decl.type?.let(::visitTypeRef) ?: value.ty
    val isNested = !currentScope.isTopLevelScope

    if (ty != value.ty) {
      return value.violate(TypeMismatch(ty, value.ty)).stmt()
    }

    currentScope.declare(name, value, mutable)

    return ResolvedLetDecl(name, mutable, value, isNested, value.ty, Subst(), decl.location)
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): Ty {
    val path = ref.path.toIdentifier()

    if (currentScope.findTyInfo(path) == null) {
      return if (ref.path.fullPath.size > 1) {
        ref.violate(UnresolvedTypeAccess(path)).ty
      } else {
        VarTy(path.text)
      }
    }

    return ConstTy(ref.path.text)
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): Ty {
    return PtrTy(visitTypeRef(ref.type))
  }

  override fun visitApplyTypeRef(ref: ApplyTypeRef): Ty {
    return visitTypeRefs(ref.arguments).fold(ConstTy(ref.function.text) as Ty) { acc, ty ->
      AppTy(acc, ty)
    }
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef): Ty {
    return FunTy(visitTypeRef(ref.returnType), visitTypeRef(ref.parameterType))
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef): Ty {
    return unitTy
  }

  private fun unitValue(): TypedExpr {
    return TypedConstExpr(Unit, unitTy, Subst(), Location.Generated)
  }

  private fun violate(error: AnalyzerViolation, loc: Location = Location.Generated) {
    when (loc) {
      is Location.Range -> violations += error.withLocation(loc)
      is Location.Generated -> {
        if (violations.none { it.message == error.message }) {
          violations += error.withLocation(loc)
        }
      }
    }
  }

  private fun PlankElement.violatedPattern(error: AnalyzerViolation): TypedPattern {
    violations += error.withLocation(location)

    return TypedIdentPattern(Identifier("<error>"), undefTy, Subst(), location)
  }

  private fun PlankElement.violate(error: AnalyzerViolation): TypedExpr {
    violations += error.withLocation(location)

    return TypedConstExpr(Unit, undefTy, Subst(), location)
  }

  private fun undeclared(ty: Ty): TypedExpr {
    return TypedConstExpr(Unit, ty, Subst(), Location.Generated)
  }

  private val scopes: Stack<Scope> = Stack<Scope>().also { stack ->
    stack.pushLast(GlobalScope(tree))
  }

  private val violations: MutableSet<AnalyzerViolation> = mutableSetOf()

  private val currentScope: Scope get() = scopes.peekLast()
  private val currentModuleTree: ModuleTree get() = scopes.peekLast().moduleTree

  private fun Scope.deconstruct(
    pattern: Pattern,
    subject: TypedExpr,
    info: TyInfo,
  ) {
    when (pattern) {
      is IdentPattern -> {
        info.getAs<EnumInfo>()?.members?.get(pattern.name) ?: return declare(pattern.name, subject)
      }
      is NamedTuplePattern -> {
        val enum = info.getAs<EnumInfo>() ?: return run {
          subject.violate(TypeInfoCanNotBeDestructured(info))
        }

        val member = enum.members[pattern.type.toIdentifier()] ?: return run {
          pattern.type.violate(UnresolvedEnumVariant(pattern.type.toIdentifier(), enum))
        }

        pattern.properties.forEachIndexed { index, subPattern ->
          val subType = member.parameters.getOrNull(index) ?: return run {
            subPattern.violatedPattern(
              IncorrectEnumArity(member.parameters.size, index, member.name)
            )
          }

          deconstruct(subPattern, undeclared(subType), enum)
        }
      }
    }
  }

  private fun findVariable(name: Identifier): Variable {
    return currentScope.findVariable(name)
      ?: SimpleVariable(false, name, name.violate(UnresolvedVariable(name)).ty, currentScope)
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
    body: Scope.() -> T,
  ): T {
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }

  // Infer
  private fun inst(scheme: Scheme): Ty {
    val env = currentScope

    val subst = scheme.names
      .zip(scheme.names.map { env.fresh() })
      .toMap()
      .mapKeys { VarTy(it.key) }
      .toSubst()

    return scheme.ty ap subst
  }

  private fun unify(a: Ty, b: Ty): Subst {
    return when {
      a == b -> Subst()
      a is VarTy -> a bind b
      b is VarTy -> b bind a
      a is AppTy && b is AppTy -> {
        val s1 = unify(a.fn, b.fn)
        val s2 = unify(a.arg.ap(s1), b.arg.ap(s1))

        s1 compose s2
      }
      a is FunTy && b is FunTy -> {
        val s1 = unify(a.returnTy, b.returnTy)
        val s2 = unify(a.parameterTy.ap(s1), b.parameterTy.ap(s1))

        s1 compose s2
      }
      a is PtrTy && b is PtrTy -> unify(a.arg, b.arg)
      else -> {
        violate(TypeMismatch(a, b))
        Subst()
      }
    }
  }

  private infix fun VarTy.bind(other: Ty): Subst {
    return when {
      this == other -> Subst()
      name in other.ftv() -> {
        violate(TypeIsInfinite(this, other))
        Subst()
      }
      else -> Subst(name, other)
    }
  }

  private infix fun Subst.compose(other: Subst): Subst {
    return Subst((map + other.map).mapValues { it.value ap this })
  }

  private fun findTyInfo(ty: Ty): TyInfo? {
    return when (ty) {
      is AppTy -> findTyInfo(ty.fn)
      is FunTy -> null
      is PtrTy -> null
      is ConstTy -> currentScope.findTyInfo(ty.name.toIdentifier())
      is VarTy -> currentScope.findTyInfo(ty.name.toIdentifier())
    }
  }
}
