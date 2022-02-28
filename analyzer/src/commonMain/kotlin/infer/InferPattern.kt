package org.plank.analyzer.infer

import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.Pattern

fun Infer.inferPattern(env: TyEnv, pattern: Pattern, subject: Ty): TyEnv {
  return when (pattern) {
    is IdentPattern -> {
      env.lookup(pattern.name.text) ?: return env.extend(pattern.name.text, env.generalize(subject))

      env
    }
    is EnumVariantPattern -> {
      val scheme = env.lookup(pattern.type.text) ?: throw UnboundVar(pattern.type.toIdentifier())
      val parameters = instantiate(scheme).callable().chainParameters()

      pattern.properties.foldIndexed(env) { i, acc, next ->
        val tv = parameters.elementAtOrNull(i) ?: throw IncorrectEnumArity(i + 1, pattern.type.text)

        inferPattern(acc, next, tv)
      }
    }
  }
}
