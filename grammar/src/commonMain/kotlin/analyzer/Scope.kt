package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PlankType

class Scope(
  val name: String,
  private val nested: Boolean,
  private val enclosing: Scope?
) {
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

  fun findStructure(name: String): PlankType.Struct? {
    return types[name]
      ?: enclosing?.findStructure(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStructure(name) }.firstOrNull()
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
