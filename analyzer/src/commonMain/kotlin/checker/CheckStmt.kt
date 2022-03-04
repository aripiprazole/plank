package org.plank.analyzer.checker

import org.plank.analyzer.element.ResolvedDecl
import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.element.ResolvedUseDecl
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.infer.chainExecution
import org.plank.analyzer.infer.ty
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.ReturnStmt
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.UseDecl
import org.plank.syntax.element.toIdentifier

fun TypeCheck.checkStmt(stmt: Stmt): ResolvedStmt {
  return when (stmt) {
    is ReturnStmt -> ResolvedExprStmt(checkExpr(stmt.value ?: ConstExpr(Unit)), stmt.loc)
    is ExprStmt -> ResolvedExprStmt(checkExpr(stmt.expr), stmt.loc)

    is UseDecl -> {
      val module = scope.lookupModule(stmt.path.toIdentifier())
        ?: return violate(stmt.path, UnresolvedModule(stmt.path.toIdentifier()))

      scope.expand(module)

      ResolvedUseDecl(module, stmt.loc)
    }

    is StructDecl -> {
      val scheme = stmt.generics
        .fold(ConstTy((scope.fullPath() + stmt.name.text).text) as Ty) { acc, n ->
          AppTy(acc, VarTy(n.text))
        }
        .generalize()

      val ty = instantiate(scheme).also {
        scope.createTyInfo(StructInfo(scope, stmt.name, it, stmt.generics, scheme))
      }

      val members = stmt.properties.associate { (mutable, name, type) ->
        name to StructMemberInfo(scope, name, checkTy(type.ty()), mutable)
      }

      val info = scope.createTyInfo(
        StructInfo(scope, stmt.name, ty, stmt.generics, scheme, members),
      )

      ResolvedStructDecl(info, stmt.loc)
    }

    is ModuleDecl -> {
      val module = scope.createModule(ModuleScope(stmt.path.toIdentifier(), scope))

      val content = scoped(module) {
        stmt.content.map(::checkStmt).filterIsInstance<ResolvedDecl>()
      }

      ResolvedModuleDecl(stmt.path, content, stmt.loc)
    }

    is LetDecl -> {
      val (t1, s1) = infer(stmt.value)
      val t2 = stmt.type?.ty()?.let(::checkTy) ?: t1

      if (t2 != t1) {
        violate<ResolvedDecl>(stmt.value, TypeMismatch(t2, t1))
      }

      val value = checkExpr(stmt.value)

      val scheme = scope.declare(stmt.name, t2.generalize(), stmt.mutable)

      ResolvedLetDecl(
        name = stmt.name,
        value = value,
        scheme = scheme,
        ty = t2,
        isNested = !scope.isTopLevelScope,
        mutable = stmt.mutable,
        subst = s1,
        loc = stmt.loc,
      )
    }

    is EnumDecl -> {
      val scheme = stmt.generics
        .fold(ConstTy((scope.fullPath() + stmt.name.text).text) as Ty) { acc, n ->
          AppTy(acc, VarTy(n.text))
        }
        .generalize()

      val ty = instantiate(scheme)
      val enum = scope.createTyInfo(EnumInfo(scope, stmt.name, ty, stmt.generics, scheme))

      val members = stmt.members.associate { (name, params) ->
        val funTy = FunTy(ty, params.ty().map(::checkTy))
        val funScheme = instantiate(funTy.generalize()).generalize()

        val memberName = (scope.fullPath() + name).text

        name to when {
          params.isEmpty() -> {
            val inst = instantiate(scheme.replaceLastName(memberName))
            val s = unify(ty, funScheme.ty.chainExecution().last())
            val info = EnumMemberInfo(scope, name, inst, scheme, funTy, s, enum, emptyList()).also {
              scope.createTyInfo(it)
            }

            scope.declare(EnumConstructor(info, scheme, name, scope))
            info
          }
          else -> {
            val inst = instantiate(funScheme.replaceLastName(memberName)).chainExecution().last()
            val s = unify(ty, funScheme.ty.chainExecution().last())
            val info = EnumMemberInfo(scope, name, inst, funScheme, funTy, s, enum).also {
              scope.createTyInfo(it)
            }

            scope.declare(EnumConstructor(info, funScheme, name, scope))
            info
          }
        }
      }
      val info = scope.createTyInfo(EnumInfo(scope, stmt.name, ty, stmt.generics, scheme, members))

      ResolvedEnumDecl(info, stmt.loc)
    }

    is FunDecl -> {
      if (scope.containsVariable(stmt.name)) return violate(stmt.name, Redeclaration(stmt.name))

      val returnTy = checkTy(stmt.returnType.ty())
      val parameters = stmt.parameters.values.ty().ifEmpty { listOf(unitTy) }.map(::checkTy)
      val ty = FunTy(returnTy, parameters)
      val scheme = scope.declare(stmt.name, ty.generalize())

      val isNested = !scope.isTopLevelScope
      val names = scheme.names.map { it.toIdentifier() }.toSet()

      val info = FunctionInfo(
        declaredIn = scope,
        name = stmt.name,
        ty = ty,
        scheme = scheme,
        generics = names,
        returnTy = returnTy,
        parameters = stmt.parameters.keys
          .zip(parameters)
          .ifEmpty { listOf("_".toIdentifier() to unitTy) }
          .toMap(),
      )
      val references = mutableMapOf<Identifier, Ty>()
      val scope = FunctionScope(
        function = info,
        enclosing = scope,
        references = references,
      )
      val body = scoped(scope) {
        stmt.parameters.forEach { (name, type) ->
          declare(name, checkTy(type.ty()))
        }

        checkBody(stmt.body)
      }

      ResolvedFunDecl(body, stmt.attributes, references, info, isNested, stmt.loc)
    }
  }
}

fun Scheme.replaceLastName(name: String): Scheme {
  return copy(ty = ty.replaceLastName(name))
}

fun Ty.lastName(): String {
  return when (this) {
    is AppTy -> fn.lastName()
    is ConstTy -> name
    is VarTy -> name
    is PtrTy -> arg.lastName()
    is FunTy -> returnTy.lastName()
  }
}

fun Ty.replaceLastName(name: String): Ty {
  return when (this) {
    is AppTy -> copy(fn = fn.replaceLastName(name))
    is ConstTy -> copy(name = name)
    is FunTy -> copy(returnTy = returnTy.replaceLastName(name))
    is VarTy -> copy(name = name)
    is PtrTy -> copy(arg = arg.replaceLastName(name))
  }
}
