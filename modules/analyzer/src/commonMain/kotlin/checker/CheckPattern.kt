package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedEnumIndexAccess
import org.plank.analyzer.element.TypedEnumVariantPattern
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.chainExecution
import org.plank.analyzer.infer.chainParameters
import org.plank.analyzer.infer.enumVariant
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
        ?: return TypedIdentPattern(name, subject.ty, location)

      val info = variable.info
      val scheme = variable.scheme

      val ty = instantiate(scheme)

      TypedEnumVariantPattern(info, name.toQualifiedPath(), emptyList(), ty, location)
    }
    is EnumVariantPattern -> {
      val name = pattern.type.toIdentifier()

      val variable = scope.lookupVariable(name) as? EnumConstructor
        ?: return violate(pattern.type, UnresolvedEnumVariant(name))

      val info = variable.info
      val scheme = variable.scheme

      val t1 = instantiate(scheme).enumVariant()
      val params = t1.chainParameters()
      val s1 = when {
        params.isEmpty() -> unify(t1, subject.ty)
        else -> unify(t1, FunTy(subject.ty, params))
      }
      val t2 = t1 ap s1
      val parameters = t2.chainParameters()
      val t3 = t2.chainExecution().last()

      val properties = pattern.properties.mapIndexed { i, next ->
        val tv = parameters.elementAtOrNull(i)
          ?: return violate(next, IncorrectEnumArity(parameters.size, i + 1, name))

        checkPattern(next, TypedEnumIndexAccess(subject, i, tv, next.loc))
      }

      val newInfo = info.copy(
        ty = info.ty ap unify(t3.replaceLastName(info.ty.lastName()), info.ty),
      )

      TypedEnumVariantPattern(newInfo, pattern.type, properties, t3, pattern.loc)
    }
  }
}
