package org.plank.analyzer.resolver

import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.NoBody
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.Stmt

fun resolveImports(file: PlankFile, tree: ModuleTree = ModuleTree()): ResolveResult {
  val resolver = ResolveImports(file, tree)

  return ResolveResult(tree, resolver.resolve(), resolver.dependencies.reversed().toSet())
}

data class ResolveResult(val tree: ModuleTree, val file: PlankFile, val dependencies: Set<Module>)

class ResolveImports(val file: PlankFile, val tree: ModuleTree) {
  val modules = tree.modules.values.toList()
  val dependencies = mutableListOf<Module>()

  init {
    dependencies.addAll(searchDependencies())
  }

  fun resolve(): PlankFile {
    val module = requireNotNull(tree.findModule(file.module)) { "Could not find file in tree" }
    val scope = module.scope as? FileScope ?: error("File scope expected")

    return resolveUses(scope.file, dependencies, tree, scope).also { file ->
      scope.file = file
      dependencies.reverse()
      dependencies.add(0, module)
    }
  }

  fun searchDependencies(): List<Module> = tree.dependencies
    .apply {
      addVertex(file.module)

      modules.forEach { module ->
        runDependencyTreeWalker((module.scope as FileScope).file)

        val scope = module.scope

        if (scope is FileScope) {
          scope.file = resolveUses(scope.file, dependencies, tree, scope)
        }
      }

      runDependencyTreeWalker(file)
    }
    .depthFirstSearch(file.module)
    .filter { it != file.module }
    .mapNotNull(tree::findModule)

  fun Graph<Identifier>.runDependencyTreeWalker(f: PlankFile) {
    val module = tree.findModule(f.module)
      ?.apply { scope = FileScope(f, this, tree.globalScope) }
      ?: Module(f.module, f.program).apply {
        tree.createModule(this)

        scope = FileScope(f, this, tree.globalScope)
      }

    val scope = module.scope as FileScope

    walkDependencyTree(f, this@runDependencyTreeWalker, tree, scope)
  }
}

fun qualifiedPath(vararg ids: Identifier): QualifiedPath {
  if (ids.isEmpty()) return QualifiedPath(emptyList())

  val first = ids.first()
  val last = ids.last()

  return QualifiedPath(ids.toList(), first.loc.endIn(last.loc))
}

fun Scope.fullPath(): QualifiedPath = when (this) {
  is ModuleScope -> QualifiedPath(module.name.text, module.name.loc)
  is FileScope -> QualifiedPath(module.name.text, module.name.loc)
  else -> QualifiedPath()
}

fun statements(body: FunctionBody): List<Stmt> = when (body) {
  is CodeBody -> body.stmts
  is ExprBody -> emptyList()
  is NoBody -> emptyList()
}

tailrec fun concatModule(expr: Expr, list: List<Identifier> = emptyList()): List<Identifier> {
  return when (expr) {
    is AccessExpr -> list + expr.name
    is GetExpr -> concatModule(expr.receiver, list + expr.property)
    else -> emptyList()
  }
}
