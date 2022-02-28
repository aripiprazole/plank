package org.plank.analyzer.resolver

import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.Decl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.Stmt

data class Module(val name: Identifier, val content: List<Stmt>) {
  lateinit var scope: Scope

  override fun toString(): String = "Module($name, scope: $scope)"
}

class ModuleTree(scopes: List<Scope> = emptyList(), val enclosing: ModuleTree?) {
  val globalScope: GlobalScope = GlobalScope(this)

  val dependencies = Graph<Identifier>().apply {
    scopes.map(Scope::name).forEach(this::addVertex)
  }

  private val _modules = mutableMapOf<Identifier, Module>()
  val modules: Map<Identifier, Module> get() = _modules

  init {
    scopes.forEach { file ->
      _modules[file.name] = Module(file.name, file.content).apply {
        scope = file.enclose(globalScope)
      }
    }
  }

  constructor(enclosing: ModuleTree, scopes: List<Scope> = emptyList()) : this(scopes, enclosing)

  constructor(files: List<PlankFile>) : this(emptyList<Scope>(), null) {
    files
      .map { Module(it.module, it.program).apply { scope = FileScope(it, this, globalScope) } }
      .map { it.scope }
      .forEach { module ->
        dependencies.addVertex(module.name)

        _modules[module.name] = Module(module.name, module.content).apply {
          scope = module
        }
      }
  }

  constructor(vararg files: PlankFile) : this(emptyList<Scope>(), null) {
    files
      .map { Module(it.module, it.program).apply { scope = FileScope(it, this, globalScope) } }
      .map { it.scope }
      .forEach { module ->
        dependencies.addVertex(module.name)

        _modules[module.name] = Module(module.name, module.content).apply {
          scope = module
        }
      }
  }

  fun findFiles(): List<PlankFile> {
    return _modules.values
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file) + enclosing?.findFiles().orEmpty()
  }

  fun addDependency(scope: Scope, on: Scope) {
    dependencies.addEdge(scope.name, on.name)
  }

  fun createModule(module: Module): Module {
    dependencies.addVertex(module.name)

    _modules[module.name] = module

    return module
  }

  fun createModule(name: Identifier, enclosing: Scope, content: List<Decl>): Module {
    dependencies.addVertex(name)

    val module = Module(name, content).apply {
      scope = ModuleScope(this, enclosing, this@ModuleTree)
    }

    _modules[name] = module

    return module
  }

  fun findModule(name: Identifier): Module? {
    return dependencies.depthFirstSearch(name).firstOrNull()?.let(_modules::get)
      ?: enclosing?.findModule(name)
  }

  fun contains(name: Identifier): Boolean {
    return findModule(name) != null
  }

  override fun toString(): String {
    val unsignedHashCode = hashCode().toLong() and 0xffffffffL
    val hashCodeStr = unsignedHashCode.toString(16)

    return "ModuleTree@$hashCodeStr"
  }
}
