package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedEnumIndexAccess
import org.plank.analyzer.element.TypedEnumVariantPattern
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.ap
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
      val location = pattern.loc

      scope.declare(name, subject.ty)

      val variable = scope.lookupVariable(name) as? EnumConstructor
        ?: return TypedIdentPattern(name, subject.ty, subject.subst, location)

      val ty = instantiate(variable.scheme)

      TypedEnumVariantPattern(name.toQualifiedPath(), emptyList(), ty, nullSubst(), location)
    }
    is EnumVariantPattern -> {
      val name = pattern.type.toIdentifier()

      val scheme = scope.lookupVariable(name)
        ?.scheme
        ?: return violate(pattern.type, UnresolvedEnumVariant(name))

      val t1 = instantiate(scheme).enumVariant()
      val params = t1.chainParameters()
      val s1 = when {
        params.isEmpty() -> unify(t1, subject.ty)
        else -> unify(t1, FunTy(subject.ty, params))
      }
      val t2 = t1 ap s1
      val parameters = t2.chainParameters()

      val properties = pattern.properties.mapIndexed { i, next ->
        val tv = parameters.elementAtOrNull(i)
          ?: return violate(next, IncorrectEnumArity(parameters.size, i + 1, name))

        checkPattern(next, TypedEnumIndexAccess(subject, i, tv, nullSubst(), next.loc))
      }

      TypedEnumVariantPattern(pattern.type, properties, t2, nullSubst(), pattern.loc)
    }
  }
}
