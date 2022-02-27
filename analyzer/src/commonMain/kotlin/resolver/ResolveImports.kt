package org.plank.analyzer.resolver

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.arr
import org.plank.analyzer.infer.undefTy
import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.Decl
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.GenericTypeRef
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.NoBody
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.TreeTransformer
import org.plank.syntax.element.TreeWalker
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UseDecl
import org.plank.syntax.element.plus

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

    return UseResolver(module.scope).visitPlankFile(file).also { file ->
      scope.file = file
      dependencies.reverse()
      dependencies.add(0, module)
    }
  }

  fun searchDependencies(): List<Module> {
    return tree.dependencies
      .apply {
        addVertex(file.module)

        modules.forEach { module ->
          runDependencyTreeWalker((module.scope as FileScope).file)

          val scope = module.scope

          if (scope is FileScope) {
            scope.file = UseResolver(scope).visitPlankFile(scope.file)
          }
        }

        runDependencyTreeWalker(file)
      }
      .depthFirstSearch(file.module)
      .filter { it != file.module }
      .mapNotNull(tree::findModule)
  }

  fun Graph<Identifier>.runDependencyTreeWalker(f: PlankFile) {
    val module = tree.findModule(f.module)
      ?.apply { scope = FileScope(f, this, tree.globalScope) }
      ?: Module(f.module, f.program).apply {
        tree.createModule(this)

        scope = FileScope(f, this, tree.globalScope)
      }

    val scope = module.scope as FileScope

    DependencyTreeWalker(this@runDependencyTreeWalker, scope).walk(f)
  }

  inner class UseResolver(var scope: Scope) : TreeTransformer() {
    inline fun <A> scoped(scope: Scope, block: Scope.() -> A): A = this.scope.let { oldScope ->
      this.scope = scope
      val value = scope.block()
      this.scope = oldScope
      value
    }

    override fun transformStructDecl(decl: StructDecl): Decl = decl.apply {
      scope.create(StructInfo(scope, decl.name, undefTy))
    }

    override fun transformEnumDecl(decl: EnumDecl): Decl = decl.apply {
      scope.create(EnumInfo(scope, name, undefTy))
    }

    override fun transformUseDecl(decl: UseDecl): Decl = decl.apply {
      val module = scope.findModule(path.toIdentifier()) ?: return decl

      scope.expand(module.scope)
    }

    override fun visitModuleDecl(decl: ModuleDecl): Decl {
      val module = tree.findModule(decl.path.toIdentifier())
        ?: Module(decl.path.toIdentifier(), decl.content).apply {
          scope = ModuleScope(this, this@UseResolver.scope, ModuleTree(tree))

          this@UseResolver.scope.tree.createModule(this)
        }

      return scoped(module.scope) { super.visitModuleDecl(decl) }
    }

    override fun visitFunDecl(decl: FunDecl): Decl {
      val info = FunctionInfo(scope, decl.name, undefTy arr undefTy, Scheme(undefTy arr undefTy))
      val scope = FunctionScope(info, statements(decl.body), scope, tree = ModuleTree(scope.tree))

      scope.declare(decl.name, info.ty)

      return scoped(scope) {
        decl.parameters.keys.forEach {
          scope.declare(it, undefTy)
        }

        super.visitFunDecl(decl)
      }
    }

    override fun transformAccessTypeRef(ref: AccessTypeRef): TypeRef {
      return when {
        ref.path.fullPath.size > 1 -> {
          val name = ref.path.fullPath.last()
          val module = ref.path.copy(fullPath = ref.path.fullPath.dropLast(1))

          val scope = scope.findModule(module.toIdentifier())?.scope ?: return ref
          val info = scope.findTyInfo(name) ?: return ref

          ref.copy(path = info.declaredIn.fullPath() + name)
        }
        else -> {
          val name = ref.path.toIdentifier()
          val info = scope.findTyInfo(ref.path.toIdentifier())
            ?: return GenericTypeRef(name, ref.location)

          ref.copy(path = info.declaredIn.fullPath() + name)
        }
      }
    }

    override fun transformGetExpr(expr: GetExpr): Expr {
      return when (val receiver = expr.receiver) {
        is GetExpr -> {
          val chain = concatModule(receiver).asReversed().ifEmpty { return expr }
          val path = QualifiedPath(chain)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AccessExpr(name = expr.property, module = path, location = expr.location)
        }
        is AccessExpr -> {
          val fullPath = receiver.module?.fullPath.orEmpty().toTypedArray()
          val path = qualifiedPath(*fullPath, receiver.name)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AccessExpr(name = expr.property, module = path, location = expr.location)
        }
        else -> expr
      }
    }
  }

  inner class DependencyTreeWalker(val graph: Graph<Identifier>, var scope: Scope) : TreeWalker() {
    override fun walkUseDecl(decl: UseDecl) {
      graph.addEdge(file.module, decl.path.toIdentifier())
    }

    override fun visitModuleDecl(decl: ModuleDecl) {
      val path = scope.name.plus(decl.path)

      val module = Module(path.toIdentifier(), decl.content).apply {
        scope = ModuleScope(this, this@DependencyTreeWalker.scope, ModuleTree(tree))
      }

      tree.createModule(module)

      scoped(module.scope) {
        super.visitModuleDecl(decl)
      }
    }

    inline fun <A> scoped(scope: Scope, block: Scope.() -> A): A = this.scope.let { oldScope ->
      this.scope = scope
      val value = scope.block()
      this.scope = oldScope
      value
    }
  }
}

fun qualifiedPath(vararg ids: Identifier): QualifiedPath {
  if (ids.isEmpty()) return QualifiedPath(emptyList())

  val first = ids.first()
  val last = ids.last()

  return QualifiedPath(ids.toList(), first.location.endIn(last.location))
}

fun Scope.fullPath(): QualifiedPath = when (this) {
  is ModuleScope -> QualifiedPath(module.name.text, module.name.location)
  is FileScope -> QualifiedPath(module.name.text, module.name.location)
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
