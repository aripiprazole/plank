package com.lorenzoog.jplank.intellijplugin.analyzer

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.PlankType
import com.lorenzoog.jplank.analyzer.Variable

data class AnalyzerScope(
  val name: String? = null,
  val nested: Boolean = true,
  val enclosing: Scope? = null
) : Scope {
  private val types = mutableMapOf<String, PlankType.Struct>()
  private val variables = mutableMapOf<String, Variable>()
  private val expanded = mutableListOf<Scope>()

  override fun expand(another: Scope): Scope {
    expanded += another

    return this
  }

  override fun declare(name: String, type: PlankType, mutable: Boolean) {
    variables[name] = Variable(mutable, type)
  }

  override fun define(name: String, type: PlankType.Struct) {
    types[name] = type
  }

  override fun findType(name: String): PlankType? {
    return Builtin.values[name] ?: findStruct(name)
  }

  override fun findStruct(name: String): PlankType.Struct? {
    return types[name]
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStruct(name) }.firstOrNull()
  }

  override fun findVariable(name: String): Variable? {
    return variables[name]
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findVariable(name) }.firstOrNull()
  }

  override fun findFunction(name: String): PlankType.Callable? {
    return findVariable(name)?.type as? PlankType.Callable
  }

  override fun lookup(): List<LookupResult> {
    val functions = variables.filter { it.value.type is PlankType.Callable }
      .map { (name, variable) ->
        LookupResult(
          kind = LookupResult.Kind.Function,
          name,
          type = (variable.type as PlankType.Callable).returnType,
          extraParams = this@AnalyzerScope.name ?: "anonymous"
        )
      }

    val variables = variables.filter { it.value.type !is PlankType.Callable }
      .map { (name, variable) ->
        LookupResult(
          kind = LookupResult.Kind.Variable,
          name,
          type = variable.type,
          extraParams = this@AnalyzerScope.name ?: "anonymous"
        )
      }

    val types = types.map { (name, type) ->
      LookupResult(
        kind = LookupResult.Kind.Struct,
        name,
        type,
        extraParams = this@AnalyzerScope.name ?: "anonymous"
      )
    }

    return (variables + functions + types)
  }
}
