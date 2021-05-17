package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.shared.Graph
import com.lorenzoog.plank.shared.depthFirstSearch

data class Module(val name: String, val content: List<Decl>) {
  lateinit var scope: Scope

  val type: PlankType.Module
    get() {
      val variables = scope.variables.map { (name, variable) ->
        PlankType.Struct.Field(variable.mutable, name, variable.type)
      }

      return PlankType.Module(name, variables)
    }

  override fun toString(): String = "Module($name, ${scope::class.simpleName})"
}

class ModuleTree(files: List<PlankFile> = emptyList()) {
  private val modules = mutableMapOf<String, Module>()

  init {
    files.forEach { file ->
      modules[file.module] = Module(file.module, file.program).apply {
        scope = FileScope(file)
      }
    }
  }

  val dependencies = Graph<String>().apply {
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

  fun createModule(name: String, enclosing: Scope, content: List<Decl>): Module {
    dependencies.addVertex(name)

    val module = Module(name, content).apply {
      scope = ModuleScope(this, enclosing, ModuleTree())
    }

    modules[name] = module

    return module
  }

  fun findModule(name: String): Module? {
    return dependencies.depthFirstSearch(name).firstOrNull()?.let(modules::get)
  }
}
