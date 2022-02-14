@file:Suppress("MaxLineLength", "MaximumLineLength")

package org.plank.analyzer.infer

import org.plank.analyzer.BindingViolation
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedPattern
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
class Infer(tree: ModuleTree) :
  PlankFile.Visitor<ResolvedPlankFile>,
  Expr.Visitor<TypedExpr>,
  Stmt.Visitor<ResolvedStmt>,
  Pattern.Visitor<TypedPattern>,
  FunctionBody.Visitor<ResolvedFunctionBody>,
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
    TODO("Not yet implemented")
  }

  override fun visitBlockExpr(expr: BlockExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitMatchExpr(expr: MatchExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitIfExpr(expr: IfExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitConstExpr(expr: ConstExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitAccessExpr(expr: AccessExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitCallExpr(expr: CallExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitAssignExpr(expr: AssignExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitSetExpr(expr: SetExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitGetExpr(expr: GetExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitGroupExpr(expr: GroupExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitInstanceExpr(expr: InstanceExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitSizeofExpr(expr: SizeofExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitRefExpr(expr: RefExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitDerefExpr(expr: DerefExpr): TypedExpr {
    TODO("Not yet implemented")
  }

  override fun visitNoBody(body: NoBody): ResolvedFunctionBody {
    TODO("Not yet implemented")
  }

  override fun visitExprBody(body: ExprBody): ResolvedFunctionBody {
    TODO("Not yet implemented")
  }

  override fun visitCodeBody(body: CodeBody): ResolvedFunctionBody {
    TODO("Not yet implemented")
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): TypedPattern {
    TODO("Not yet implemented")
  }

  override fun visitIdentPattern(pattern: IdentPattern): TypedPattern {
    TODO("Not yet implemented")
  }

  override fun visitExprStmt(stmt: ExprStmt): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitReturnStmt(stmt: ReturnStmt): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitUseDecl(decl: UseDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitModuleDecl(decl: ModuleDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitEnumDecl(decl: EnumDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitStructDecl(decl: StructDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitFunDecl(decl: FunDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitLetDecl(decl: LetDecl): ResolvedStmt {
    TODO("Not yet implemented")
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): Ty {
    TODO("Not yet implemented")
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): Ty {
    TODO("Not yet implemented")
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef): Ty {
    TODO("Not yet implemented")
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef): Ty {
    TODO("Not yet implemented")
  }

  override fun visitUnitTypeRef(ref: UnitTypeRef): Ty {
    TODO("Not yet implemented")
  }

  private fun PlankElement.violatedPattern(message: String): TypedPattern {
    violations += BindingViolation(message, location)

    return TypedIdentPattern(Identifier("<error>"), undefTy, location)
  }

  private fun PlankElement.violate(message: String): TypedExpr {
    violations += BindingViolation(message, location)

    return TypedConstExpr(Unit, undefTy, location)
  }

  private fun undeclared(ty: Ty): TypedExpr {
    return TypedConstExpr(Unit, ty, Location.Generated)
  }

  private val scopes = Stack<Scope>().also { stack ->
    stack.pushLast(GlobalScope(tree))
  }

  private val violations = mutableSetOf<BindingViolation>()

  private val currentScope get() = scopes.peekLast()
  private val currentModuleTree get() = scopes.peekLast().moduleTree

  private fun findVariable(name: Identifier): Variable {
    return currentScope.findVariable(name)
      ?: Variable(false, name, name.violate("Unresolved variable `${name.text}`").ty, currentScope)
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
