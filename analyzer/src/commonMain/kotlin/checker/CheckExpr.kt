package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.arr
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.chainParameters
import org.plank.analyzer.infer.nullSubst
import org.plank.analyzer.infer.ty
import org.plank.analyzer.infer.unitTy
import org.plank.analyzer.resolver.ClosureScope
import org.plank.analyzer.resolver.PatternScope
import org.plank.analyzer.resolver.StructInfo
import org.plank.analyzer.resolver.getAs
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.Expr
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.GroupExpr
import org.plank.syntax.element.IfExpr
import org.plank.syntax.element.InstanceExpr
import org.plank.syntax.element.MatchExpr
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.element.orEmpty
import org.plank.syntax.element.toIdentifier

fun TypeCheck.checkExpr(expr: Expr): TypedExpr {
  return when (expr) {
    is SizeofExpr -> TypedSizeofExpr(checkTy(expr.type.ty()), nullSubst(), expr.loc)
    is RefExpr -> TypedRefExpr(checkExpr(expr.value), expr.loc)
    is GroupExpr -> TypedGroupExpr(checkExpr(expr.value), expr.loc)

    is DerefExpr -> {
      val value = checkExpr(expr.value)
      val ty = value.ty as? PtrTy ?: return violate(expr.value, TypeIsNotPointer(value.ty))

      TypedDerefExpr(value, ty.arg, value.subst, expr.loc)
    }

    is ConstExpr -> {
      val (ty, s) = infer(expr)

      TypedConstExpr(expr.value, ty, s, expr.loc)
    }

    is BlockExpr -> scoped(ClosureScope("Block".toIdentifier(), expr.stmts, scope)) {
      val stmts = expr.stmts.map(::checkStmt)
      val value = checkExpr(expr.value ?: ConstExpr(Unit))

      TypedBlockExpr(stmts, value, references, expr.loc)
    }

    is AccessExpr -> {
      val variableScope = scope.findModule(expr.module.orEmpty().toIdentifier())?.scope ?: scope

      val variable = variableScope.lookupVariable(expr.name)
        ?: return violate(expr, UnresolvedVariable(expr.name))

      TypedAccessExpr(variable, instantiate(variable.scheme), nullSubst(), expr.loc)
    }

    is IfExpr -> {
      val location = expr.loc
      val cond = checkExpr(expr.cond)

      if (boolTy != cond.ty) {
        return violate(cond, TypeMismatch(boolTy, cond.ty))
      }

      val thenBranch = checkBranch(expr.thenBranch)
      val elseBranch = expr.elseBranch?.let { checkBranch(it) }
        ?: return TypedIfExpr(cond, thenBranch, null, thenBranch.ty, thenBranch.subst, location)

      val subst = unify(thenBranch.ty, elseBranch.ty) compose
        thenBranch.subst compose
        elseBranch.subst

      val ty = thenBranch.ty ap subst
      if (ty != elseBranch.ty ap subst) {
        return violate(elseBranch, TypeMismatch(ty, elseBranch.ty ap subst))
      }

      TypedIfExpr(cond, thenBranch, elseBranch, ty, subst, location) ap subst
    }

    is AssignExpr -> {
      val value = checkExpr(expr.value)

      val scope = scope.findModule(expr.module.orEmpty().toIdentifier())?.scope ?: scope
      val variable = scope.lookupVariable(expr.name)
        ?: return violate(expr, UnresolvedVariable(expr.name))

      TypedAccessExpr(variable, instantiate(variable.scheme), nullSubst(), expr.loc)

      if (!variable.mutable) {
        violate<TypedExpr>(expr.value, CanNotReassignImmutableVariable(variable.name))
      }

      val t1 = variable.ty
      val s1 = unify(value.ty, t1)
      val s2 = value.subst compose s1

      if (t1 ap s1 != value.ty ap s1) {
        return violate(value, TypeMismatch(t1 ap s1, value.ty ap s1))
      }

      return TypedAssignExpr(scope, variable.name, value, value.ty, s2, expr.loc)
    }

    is GetExpr -> {
      val receiver = checkExpr(expr.receiver)
      val property = expr.property

      val info = lookupInfo(receiver.ty)
        ?: return violate(receiver, UnresolvedType(receiver.ty))

      val struct = info.getAs<StructInfo>()
        ?: return violate(receiver, TypeIsNotStructAndCanNotGet(expr.property, receiver.ty))

      val member = struct.members[expr.property]
        ?: return violate(property, UnresolvedStructMember(expr.property, struct))

      TypedGetExpr(receiver, member.name, struct, member.ty, nullSubst(), expr.loc)
    }

    is SetExpr -> {
      val receiver = checkExpr(expr.receiver)
      val value = checkExpr(expr.value)
      val property = expr.property

      val info = lookupInfo(receiver.ty)
        ?: return violate(receiver, UnresolvedType(receiver.ty))

      val struct = info.getAs<StructInfo>()
        ?: return violate(receiver, TypeIsNotStructAndCanNotGet(expr.property, receiver.ty))

      val member = struct.members[expr.property]
        ?: return violate(property, UnresolvedStructMember(expr.property, struct))

      if (!member.mutable) {
        violate<TypedExpr>(property, CanNotReassignImmutableStructMember(member.name, struct))
      }

      if (member.ty != value.ty) {
        violate<TypedExpr>(value, TypeMismatch(member.ty, value.ty))
      }

      TypedSetExpr(receiver, member.name, value, struct, member.ty, value.subst, expr.loc)
    }

    is InstanceExpr -> {
      val structTy = checkTy(expr.type.ty())

      val info = lookupInfo(structTy) ?: return violate(expr.type, UnresolvedType(structTy))
      val struct = info.getAs<StructInfo>() ?: return violate(expr.type, TypeIsNotStruct(structTy))

      val ty = instantiate(
        Scheme(
          struct.generics.map { it.text }.toSet(),
          struct.generics.fold(ConstTy(struct.name.text) as Ty) { acc, next ->
            AppTy(acc, VarTy(next.text))
          }
        ),
      )

      var subst = Subst()
      val arguments = expr.arguments.mapValues { (name, expr) ->
        val value = checkExpr(expr)
        val property = struct.members[name]
          ?: return violate(name, UnresolvedStructMember(name, struct))

        subst = subst compose unify(property.ty, value.ty)

        val propertyTy = property.ty ap subst
        if (propertyTy != value.ty) {
          return violate(value, TypeMismatch(propertyTy, value.ty))
        }

        value
      }

      TypedInstanceExpr(arguments, struct, ty, subst, expr.loc) ap subst
    }

    is CallExpr -> {
      val callee = checkExpr(expr.callee)
      val ty = callee.ty as? FunTy ?: return violate(callee, TypeIsNotCallable(callee.ty))
      val parameters = ty.chainParameters()

      expr.arguments
        .ifEmpty { listOf(ConstExpr(Unit)) }
        .map { checkExpr(it) }
        .foldIndexed(callee) { i, acc, argument ->
          val t1 = acc.ty
          val s1 = acc.subst

          val tv = fresh()

          val t2 = parameters.elementAtOrNull(i) ?: run {
            violate<TypedExpr>(argument, IncorrectArity(parameters.size, i + 1))
            argument.ty
          }
          val s2 = unify(t2, argument.ty)
          val s3 = unify((t2 ap s2) arr tv, t1 ap s2)

          TypedCallExpr(acc, argument, tv ap s3, nullSubst(), expr.loc) ap
            (s3 compose s2 compose s1)
        }
    }

    is MatchExpr -> {
      val subject = checkExpr(expr.subject)

      when {
        expr.patterns.isEmpty() -> {
          TypedMatchExpr(subject, emptyMap(), unitTy, Subst(), expr.loc)
        }
        else -> {
          val patterns = expr.patterns.entries.associate { (pattern, value) ->
            scoped(PatternScope(pattern, scope)) {
              checkPattern(pattern, subject) to checkExpr(value)
            }
          }

          var s = nullSubst()
          val fst = patterns.values.first()
          val ty = patterns.values.drop(1).fold(fst.ty) { acc, next ->
            s = s compose unify(next.ty, acc)

            if (acc != next.ty ap s) {
              violate<TypedExpr>(next, TypeMismatch(acc, next.ty ap s))
            }

            acc
          }

          TypedMatchExpr(subject, patterns, ty, s, expr.loc) ap s
        }
      }
    }
  }
}
