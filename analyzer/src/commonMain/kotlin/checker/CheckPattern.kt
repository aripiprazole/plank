package org.plank.analyzer.checker

import org.plank.analyzer.IncorrectEnumArity
import org.plank.analyzer.UnresolvedEnumVariant
import org.plank.analyzer.element.TypedEnumIndexAccess
import org.plank.analyzer.element.TypedEnumVariantPattern
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.infer.chainParameters
import org.plank.analyzer.infer.enumVariant
import org.plank.analyzer.infer.nullSubst
import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.toQualifiedPath

fun TypeCheck.checkPattern(pattern: Pattern, subject: TypedExpr): TypedPattern {
  return when (pattern) {
    is IdentPattern -> {
      val name = pattern.name
      val location = pattern.location

      scope.declare(name, subject.ty)

      val scheme = scope.lookupVariable(name)
        ?.scheme() ?: return TypedIdentPattern(name, subject.ty, subject.subst, location)

      val ty = infer.instantiate(scheme)

      TypedEnumVariantPattern(name.toQualifiedPath(), emptyList(), ty, nullSubst(), location)
    }
    is EnumVariantPattern -> {
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

      TypedEnumVariantPattern(pattern.type, properties, ty, nullSubst(), pattern.location)
    }
  }
}
