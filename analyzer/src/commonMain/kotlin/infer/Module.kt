package org.plank.analyzer.infer

import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.Decl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.PlankFile

data class Module(val name: Identifier, val content: List<Decl>) {
  lateinit var scope: Scope

  override fun toString(): String = "Module($name, scope=${scope::class.simpleName})"
}

class ModuleTree(files: List<PlankFile> = emptyList()) {
  private val _modules = mutableMapOf<Identifier, Module>()
  val modules: Map<Identifier, Module> get() = _modules

  init {
    files.forEach { file ->
      _modules[file.module] = Module(file.module, file.program).apply {
        scope = FileScope(file, this)
      }
    }
  }

  val dependencies = Graph<Identifier>().apply {
    files.map(PlankFile::module).forEach(this::addVertex)
  }

  fun findFiles(): List<PlankFile> {
    return _modules.values
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
  }

  fun addDependency(scope: Scope, on: Scope) {
    dependencies.addEdge(scope.name, on.name)
  }

  fun createModule(name: Identifier, enclosing: Scope, content: List<Decl>): Module {
    dependencies.addVertex(name)

    val module = Module(name, content).apply {
      scope = ModuleScope(this, enclosing, ModuleTree())
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
}
