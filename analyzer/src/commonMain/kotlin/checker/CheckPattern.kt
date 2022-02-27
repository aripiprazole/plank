package org.plank.analyzer.checker

import org.plank.analyzer.IncorrectEnumArity
import org.plank.analyzer.UnresolvedEnumVariant
import org.plank.analyzer.element.TypedEnumIndexAccess
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedNamedTuplePattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.infer.chainParameters
import org.plank.analyzer.infer.enumVariant
import org.plank.analyzer.infer.nullSubst
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.NamedTuplePattern
import org.plank.syntax.element.Pattern

fun TypeCheck.checkPattern(pattern: Pattern, subject: TypedExpr): TypedPattern {
  return when (pattern) {
    is IdentPattern -> {
      scope.declare(pattern.name, subject.ty)

      TypedIdentPattern(pattern.name, subject.ty, subject.subst, pattern.location)
    }
    is NamedTuplePattern -> {
      val name = pattern.type.toIdentifier()

      val scheme = scope.lookupVariable(name)
        ?.scheme()
        ?: return violate(pattern.type, UnresolvedEnumVariant(name))

      val ty = infer.instantiate(scheme).enumVariant()
      val parameters = ty.chainParameters()

      val properties = pattern.properties.mapIndexed { i, next ->
        val tv = parameters.elementAtOrNull(i)
          ?: return violate(next, IncorrectEnumArity(parameters.size, i + 1, name))

        checkPattern(next, TypedEnumIndexAccess(subject, i, tv, nullSubst(), next.location))
      }

      TypedNamedTuplePattern(properties, pattern.type, ty, nullSubst(), pattern.location)
    }
  }
}
