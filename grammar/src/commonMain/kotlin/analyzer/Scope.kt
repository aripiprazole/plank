package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkCallable
import com.lorenzoog.jplank.analyzer.type.PkStructure
import com.lorenzoog.jplank.analyzer.type.PkType

class Scope(val name: String, private val enclosing: Scope?) {
  private val types = mutableMapOf<String, PkStructure>()
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

  fun declare(name: String, type: PkType, mutable: Boolean = false) {
    variables[name] = Variable(mutable, type)
  }

  fun create(name: String, type: PkStructure) {
    types[name] = type
  }

  fun findType(name: String): PkType? {
    return Builtin.values[name] ?: findStructure(name)
  }

  fun findStructure(name: String): PkStructure? {
    return types[name]
      ?: enclosing?.findStructure(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStructure(name) }.firstOrNull()
  }

  fun findVariable(name: String): Variable? {
    return variables[name]
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findVariable(name) }.firstOrNull()
  }

  fun findFunction(name: String): PkCallable? {
    return findVariable(name)?.type as? PkCallable
  }
}
