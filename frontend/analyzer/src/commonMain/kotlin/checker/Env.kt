package org.plank.analyzer.checker

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.TyEnv

fun Scope.allVariables(
  original: Scope = this,
  inScope: Boolean = true,
  indent: String = "",
): Set<Variable> {
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
        else -> variable.scheme
      }
    }

  return TyEnv(map)
}
