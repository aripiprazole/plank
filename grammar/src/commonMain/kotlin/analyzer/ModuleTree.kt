package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.element.PlankFile

data class Module(
  val name: String,
  val scope: Scope,
  val content: List<Decl>
)

class ModuleTree(files: List<PlankFile>) {
  private val modules = mutableMapOf<String, Module>()

  val dependencies = Graph<String>().apply {
    files.map(PlankFile::module).forEach(this::addVertex)
  }

  fun addDependency(scope: Scope, on: Scope) {
    dependencies.addEdge(scope.name, on.name)
  }

  fun createModule(name: String, scope: Scope, content: List<Decl>): Module {
    val module = Module(name, scope, content)
    modules[name] = module
    return module
  }

  fun findModule(name: String): Module? {
    return dependencies.depthFirstSearch(name).firstOrNull()?.let(modules::get)
  }

}
