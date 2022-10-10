package org.plank.analyzer.resolver

import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.Decl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.Stmt

data class Module(val name: Identifier, val content: List<Stmt>) {
  lateinit var scope: ResolverScope

  override fun toString(): String = "Module($name, scope: $scope)"
}

class ModuleTree(scopes: List<ResolverScope> = emptyList()) {
  private val _modules: MutableMap<Identifier, Module> = mutableMapOf()

  val modules: Map<Identifier, Module> get() = _modules

  val dependencies = Graph<Identifier>().apply {
    scopes.map(ResolverScope::name).forEach(this::addVertex)
  }

  init {
    scopes.forEach { file ->
      _modules[file.name] = Module(file.name, file.content).apply {
        scope = file.enclose(GlobalScope)
      }
    }
  }

  fun findFiles(): List<PlankFile> {
    return _modules.values
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
  }

  fun addDependency(scope: ResolverScope, on: ResolverScope) {
    dependencies.addEdge(scope.name, on.name)
  }

  fun createModule(module: Module): Module {
    dependencies.addVertex(module.name)

    _modules[module.name] = module

    return module
  }

  fun createModule(name: Identifier, enclosing: ResolverScope, content: List<Decl>): Module {
    dependencies.addVertex(name)

    val module = Module(name, content).apply {
      scope = ModuleScope(name, content, enclosing, this@ModuleTree)
    }

    _modules[name] = module

    return module
  }

  fun findModule(name: Identifier): Module? {
    return dependencies.depthFirstSearch(name).firstOrNull()?.let(_modules::get)
  }

  override fun toString(): String {
    val unsignedHashCode = hashCode().toLong() and 0xffffffffL
    val hashCodeStr = unsignedHashCode.toString(16)

    return "ModuleTree@$hashCodeStr"
  }

  companion object {
    fun create(files: List<PlankFile>): ModuleTree {
      return files
        .map { Module(it.module, it.program).apply { scope = FileScope(it, GlobalScope) } }
        .map { it.scope }
        .let(::ModuleTree)
    }

    fun create(vararg files: PlankFile): ModuleTree {
      return create(files.toList())
    }
  }
}
