package org.plank.analyzer.checker

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.TyEnv
import org.plank.analyzer.resolver.GlobalScope
import org.plank.analyzer.resolver.InlineVariable
import org.plank.analyzer.resolver.LocalVariable
import org.plank.analyzer.resolver.RankedVariable
import org.plank.analyzer.resolver.Scope
import org.plank.analyzer.resolver.Variable
import org.plank.analyzer.resolver.fullPath

fun Scope.allVariables(original: Scope = this, inScope: Boolean = true, indent: String = ""): Set<Variable> {
  if (original == this && !inScope) {
    return emptySet()
  }
  if (this is GlobalScope) {
    return variables.values.toSet()
  }

  val allVariables = variables.values.toList() +
    enclosing?.allVariables(original, false, "$indent  ").orEmpty() +
    expanded.flatMap { it.allVariables(original, false, "$indent  ") }

  return allVariables.toSet()
}

fun Scope.asTyEnv(): TyEnv {
  val map = allVariables()
    .associateBy { it.declaredIn.fullPath() + it.name }
    .mapKeys { it.key.text }
    .mapValues { (_, variable) ->
      when (variable) {
        is InlineVariable -> Scheme(variable.ty)
        is LocalVariable -> Scheme(variable.ty)
        is RankedVariable -> variable.scheme
      }
    }

  return TyEnv(map)
}
