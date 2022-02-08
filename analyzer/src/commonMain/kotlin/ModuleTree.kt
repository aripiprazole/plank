package org.plank.analyzer

import org.plank.shared.Graph
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.Decl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.PlankFile

data class Module(val name: Identifier, val content: List<Decl>) {
  lateinit var scope: Scope

  val type: ModuleType
    get() {
      return ModuleType(name, scope.variables.values.toList())
    }

  override fun toString(): String = "Module($name, ${scope::class.simpleName})"
}

class ModuleTree(files: List<PlankFile> = emptyList()) {
  private val modules = mutableMapOf<Identifier, Module>()

  init {
    files.forEach { file ->
      modules[file.module] = Module(file.module, file.program).apply {
        scope = FileScope(file)
      }
    }
  }

  val dependencies = Graph<Identifier>().apply {
    files.map(PlankFile::module).forEach(this::addVertex)
  }

  fun findFiles(): List<PlankFile> {
    return modules.values
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

    modules[name] = module

    return module
  }

  fun findModule(name: Identifier): Module? {
    return dependencies.depthFirstSearch(name).firstOrNull()?.let(modules::get)
  }
}
