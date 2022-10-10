package org.plank.analyzer.resolver

import org.plank.shared.Graph
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.UseDecl
import org.plank.syntax.element.plus
import org.plank.syntax.element.walkTree

fun walkDependencyTree(
  f: PlankFile,
  graph: Graph<Identifier>,
  tree: ModuleTree,
  _scope: ResolverScope,
) {
  var currentScope = _scope

  fun addDependencyRecursive(scope: ResolverScope, path: QualifiedPath) {
    graph.addEdge(scope.name, path.toIdentifier())

    when (scope) {
      is ModuleScope -> addDependencyRecursive(scope.enclosing, path)
      else -> {}
    }
  }

  fun enterDecl(decl: Stmt) {
    when (decl) {
      is ModuleDecl -> {
        val path = currentScope.name + decl.path

        val module = Module(path.toIdentifier(), decl.content).apply {
          scope = ModuleScope(name, content, currentScope, tree)
        }

        tree.createModule(module)
        currentScope = module.scope
      }

      else -> {}
    }
  }

  fun exitDecl(decl: Stmt) {
    when (decl) {
      is UseDecl -> addDependencyRecursive(currentScope, decl.path)
      is ModuleDecl -> currentScope = currentScope.enclosing!!
      else -> {}
    }
  }

  walkTree(f, enterStmt = ::enterDecl, exitStmt = ::exitDecl)
}
