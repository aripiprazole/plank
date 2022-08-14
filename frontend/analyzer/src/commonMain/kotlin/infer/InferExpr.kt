package org.plank.analyzer.infer

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
import org.plank.analyzer.infer.ungeneralize
import org.plank.analyzer.infer.unitTy
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

fun TypeCheck.inferExpr(expr: Expr): TypedExpr {
  return when (expr) {
    is SizeofExpr -> TypedSizeofExpr(inferTy(expr.type.ty()), expr.loc)
    is RefExpr -> TypedRefExpr(inferExpr(expr.value), expr.loc)
    is GroupExpr -> TypedGroupExpr(inferExpr(expr.value), expr.loc)

    is DerefExpr -> {
      val value = inferExpr(expr.value)
      val ty = value.ty as? PtrTy ?: return violate(expr.value, TypeIsNotPointer(value.ty))

      TypedDerefExpr(value, ty.arg, expr.loc)
    }

    is ConstExpr -> {
      val (ty) = infer(expr)

      TypedConstExpr(expr.value, ty, expr.loc)
    }

    is BlockExpr -> scoped(ClosureScope("Block".toIdentifier(), scope)) {
      val stmts = expr.stmts.map(::inferStmt)
      val value = inferExpr(expr.value ?: ConstExpr(Unit))

      TypedBlockExpr(stmts, value, references, expr.loc)
    }

    is AccessExpr -> {
      val scope = scope.lookupModule(expr.module.orEmpty().toIdentifier()) ?: scope

      val variable = scope.lookupVariable(expr.name)
        ?: return violate(expr, UnresolvedVariable(expr.name))

      if (!variable.isInScope && !variable.declaredIn.isTopLevelScope) {
        scope.references[variable.name] = variable.ty
      }

      TypedAccessExpr(variable, nullSubst(), instantiate(variable.scheme), expr.loc)
    }

    is IfExpr -> {
      val location = expr.loc
      val cond = inferExpr(expr.cond)

      if (boolTy != cond.ty) {
        return violate(cond, TypeMismatch(boolTy, cond.ty))
      }

      val thenBranch = inferBranch(expr.thenBranch)
      val elseBranch = expr.elseBranch?.let { inferBranch(it) }
        ?: return TypedIfExpr(cond, thenBranch, null, thenBranch.ty, location)

      val subst = unify(thenBranch.ty, elseBranch.ty)

      val ty = thenBranch.ty ap subst
      if (ty != elseBranch.ty ap subst) {
        return violate(elseBranch, TypeMismatch(ty, elseBranch.ty ap subst))
      }

      TypedIfExpr(cond, thenBranch, elseBranch, ty, location) ap subst
    }

    is AssignExpr -> {
      val value = inferExpr(expr.value)

      val scope = scope.lookupModule(expr.module.orEmpty().toIdentifier()) ?: scope
      val variable = scope.lookupVariable(expr.name)
        ?: return violate(expr, UnresolvedVariable(expr.name))

      TypedAccessExpr(variable, nullSubst(), instantiate(variable.scheme), expr.loc)

      if (!variable.mutable) {
        violate<TypedExpr>(expr.value, CanNotReassignImmutableVariable(variable.name))
      }

      val t1 = variable.ty
      val s1 = unify(value.ty, t1)

      if (t1 ap s1 != value.ty) {
        return violate(value, TypeMismatch(t1 ap s1, value.ty))
      }

      return TypedAssignExpr(scope, variable.name, value, value.ty, expr.loc)
    }

    is GetExpr -> {
      val receiver = inferExpr(expr.receiver)
      val property = expr.property

      val info = lookupInfo(receiver.ty)
        ?: return violate(receiver, UnresolvedType(receiver.ty))

      val struct = info.getAs<StructInfo>()
        ?: return violate(receiver, TypeIsNotStructAndCanNotGet(expr.property, receiver.ty))

      val member = struct.members[expr.property]
        ?: return violate(property, UnresolvedStructMember(expr.property, struct))

      TypedGetExpr(receiver, member.name, struct, member.ty, expr.loc)
    }

    is SetExpr -> {
      val receiver = inferExpr(expr.receiver)
      val value = inferExpr(expr.value)
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

      TypedSetExpr(receiver, member.name, value, struct, member.ty, expr.loc)
    }

    is InstanceExpr -> {
      val structTy = inferTy(expr.type.ty())

      val info = lookupInfo(structTy) ?: return violate(expr.type, UnresolvedType(structTy))
      val struct = info.getAs<StructInfo>() ?: return violate(expr.type, TypeIsNotStruct(structTy))

      val ty = instantiate(
        Scheme(
          struct.generics.map { it.text }.toSet(),
          struct.generics.fold(structTy.ungeneralize() as Ty) { acc, next ->
            AppTy(acc, VarTy(next.text))
          },
        ),
      )

      var subst = Subst()
      val arguments = expr.arguments.mapValues { (name, expr) ->
        val value = inferExpr(expr)
        val property = struct.members[name]
          ?: return violate(name, UnresolvedStructMember(name, struct))

        subst = subst compose unify(property.ty, value.ty)

        val propertyTy = property.ty ap subst
        if (propertyTy != value.ty) {
          return violate(value, TypeMismatch(propertyTy, value.ty))
        }

        value
      }

      TypedInstanceExpr(arguments, struct, ty, expr.loc) ap subst
    }

    is CallExpr -> {
      val callee = inferExpr(expr.callee)
      val ty = callee.ty as? FunTy ?: return violate(callee, TypeIsNotCallable(callee.ty))
      val parameters = ty.chainParameters()

      if (
        callee is TypedAccessExpr &&
        callee.variable is InlineVariable &&
        parameters.size == expr.arguments.size
      ) {
        val variable = callee.variable
        val arguments = expr.arguments.map(::inferExpr)

        arguments.zip(parameters).forEach { (arg, param) ->
          if (param != arg.ty) {
            violate<TypedExpr>(arg, TypeMismatch(param, arg.ty))
          }
        }

        return variable.inlineCall(arguments)
      }

      expr.arguments
        .ifEmpty { listOf(ConstExpr(Unit)) }
        .map { inferExpr(it) }
        .foldIndexed(callee) { i, acc, argument ->
          val t1 = acc.ty

          val tv = fresh()

          val t2 = parameters.elementAtOrNull(i) ?: run {
            violate<TypedExpr>(argument, IncorrectArity(parameters.size, i + 1))
            argument.ty
          }
          val s1 = unify(t2, argument.ty)
          val s2 = unify((t2 ap s1) arr tv, t1 ap s1)

          TypedCallExpr(acc, argument, tv ap s2, expr.loc) ap (s2 compose s1)
        }
    }

    is MatchExpr -> {
      val subject = inferExpr(expr.subject)

      when {
        expr.patterns.isEmpty() -> {
          TypedMatchExpr(subject, emptyMap(), unitTy, expr.loc)
        }

        else -> {
          val patterns = expr.patterns.entries.associate { (pattern, value) ->
            scoped(PatternScope(pattern, scope)) {
              inferPattern(pattern, subject) to inferExpr(value)
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

          TypedMatchExpr(subject, patterns, ty ap s, expr.loc) ap s
        }
      }
    }
  }
}
