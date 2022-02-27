package org.plank.analyzer.infer

import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.NamedTuplePattern
import org.plank.syntax.element.Pattern

fun Infer.inferPattern(env: TyEnv, pattern: Pattern, subject: Ty): TyEnv = when (pattern) {
  is IdentPattern -> env.extend(pattern.name.text, env.generalize(subject))
  is NamedTuplePattern -> {
    val scheme = env.lookup(pattern.type.text) ?: throw UnboundVar(pattern.type.toIdentifier())
    val parameters = instantiate(scheme).callable().chainParameters()

    pattern.properties.foldIndexed(env) { i, acc, next ->
      val tv = parameters.elementAtOrNull(i) ?: throw IncorrectEnumArity(i + 1, pattern.type.text)

      inferPattern(acc, next, tv)
    }
  }
}
