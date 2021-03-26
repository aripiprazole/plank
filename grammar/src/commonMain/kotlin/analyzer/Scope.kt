package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.element.PlankFile

data class Variable(val mutable: Boolean, val type: PlankType)

class GlobalScope(override val moduleTree: ModuleTree) : Scope() {
  override val name: String = "Global"
  override val enclosing: Scope? = null
}

data class FileScope(
  val file: PlankFile,
  override val enclosing: Scope? = null,
  override val moduleTree: ModuleTree = ModuleTree(),
) : Scope() {
  override val name: String = file.module
  override val nested = false
}

data class ModuleScope(
  val module: Module,
  override val enclosing: Scope,
  override val moduleTree: ModuleTree = ModuleTree()
) : Scope() {
  override val name: String = "${enclosing.name}.${module.name}"
}

class FunctionScope(
  override val name: String,
  override val enclosing: Scope? = null,
  val function: PlankType.Callable,
  override val moduleTree: ModuleTree = ModuleTree(),
) : Scope() {
  val parameters = function.parameters
  val returnType = function.returnType
}

class ClosureScope(
  override val name: String,
  override val enclosing: Scope,
  override val moduleTree: ModuleTree = ModuleTree()
) : Scope()

sealed class Scope {
  abstract val name: String
  abstract val enclosing: Scope?
  abstract val moduleTree: ModuleTree
  open val nested: Boolean get() = enclosing != null

  private val types = mutableMapOf<String, PlankType.Struct>()
  private val variables = mutableMapOf<String, Variable>()
  private val expanded = mutableListOf<Scope>()

  fun expand(another: Scope): Scope {
    expanded += another

    return this
  }

  fun getScope(name: String): Scope? {
    if (variables.containsKey(name)) return this
    expanded.forEach { scope ->
      if (scope.variables.containsKey(name)) {
        return scope
      }
    }

    return enclosing?.getScope(name)
  }

  fun declare(name: String, type: PlankType, mutable: Boolean = false) {
    variables[name] = Variable(mutable, type)
  }

  fun getOrCreate(name: String): PlankType {
    return types.getOrPut(name) {
      PlankType.Struct(name)
    }
  }

  fun create(name: String, type: PlankType.Struct) {
    types[name] = type
  }

  fun findType(name: String): PlankType? {
    return Builtin.values[name] ?: findStructure(name)
  }

  fun findModule(name: String): Module? {
    return moduleTree.findModule(name)
      ?: enclosing?.findModule(name)
  }

  fun findStructure(name: String): PlankType.Struct? {
    return types[name]
      ?: enclosing?.findStructure(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStructure(name) }.firstOrNull()
  }

  // todo add usage tracker
  fun findVariableOn(scope: Scope, name: String): Variable? {
    return variables[name]
  }

  fun findVariable(name: String): Variable? {
    return variables[name]
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findVariable(name) }.firstOrNull()
  }

  fun findFunction(name: String): PlankType.Callable? {
    return findVariable(name)?.type as? PlankType.Callable
  }
}
