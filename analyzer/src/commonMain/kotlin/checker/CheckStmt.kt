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
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.ty
import org.plank.analyzer.infer.unitTy
import org.plank.analyzer.resolver.EnumInfo
import org.plank.analyzer.resolver.EnumMemberInfo
import org.plank.analyzer.resolver.FunctionInfo
import org.plank.analyzer.resolver.FunctionScope
import org.plank.analyzer.resolver.ModuleTree
import org.plank.analyzer.resolver.StructInfo
import org.plank.analyzer.resolver.StructMemberInfo
import org.plank.analyzer.resolver.fullPath
import org.plank.analyzer.resolver.statements
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
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
      val module = scope.findModule(stmt.path.toIdentifier())
        ?: return violate(stmt.path, UnresolvedModule(stmt.path.toIdentifier()))

      scope.expand(module.scope)

      ResolvedUseDecl(module, stmt.loc)
    }

    is StructDecl -> {
      val scheme = stmt.generics
        .fold(ConstTy((scope.fullPath() + stmt.name.text).text) as Ty) { acc, n ->
          AppTy(acc, VarTy(n.text))
        }
        .generalize()

      val ty = instantiate(scheme).also {
        scope.create(StructInfo(scope, stmt.name, it, stmt.generics))
      }

      val members = stmt.properties.associate { (mutable, name, type) ->
        name to StructMemberInfo(scope, name, checkTy(type.ty()), mutable)
      }
      val info = scope.create(StructInfo(scope, stmt.name, ty, stmt.generics, members))

      ResolvedStructDecl(info, stmt.loc)
    }

    is ModuleDecl -> {
      val module = scope.tree.createModule(
        name = stmt.path.toIdentifier(),
        enclosing = scope,
        content = stmt.content,
      )

      val content = scoped(module.scope) {
        stmt.content.map(::checkStmt).filterIsInstance<ResolvedDecl>()
      }

      ResolvedModuleDecl(stmt.path, content, stmt.loc)
    }

    is LetDecl -> {
      val tv = fresh()

      val (t1, s1) = infer(stmt.value)
      val t2 = stmt.type?.ty()?.let(::checkTy) ?: t1
      val s2 = unify(t2 ap s1, tv)

      val ty = t2 ap s2

      val value = checkExpr(stmt.value)

      val scheme = scope.declare(stmt.name, ty.generalize())

      ResolvedLetDecl(
        name = stmt.name,
        value = value,
        scheme = scheme,
        ty = ty,
        isNested = !scope.isTopLevelScope,
        mutable = stmt.mutable,
        subst = s2 compose s1,
        loc = stmt.loc,
      )
    }

    is EnumDecl -> {
      val scheme = stmt.generics
        .fold(ConstTy((scope.fullPath() + stmt.name.text).text) as Ty) { acc, n ->
          AppTy(acc, VarTy(n.text))
        }
        .generalize()

      val ty = instantiate(scheme).also {
        scope.create(StructInfo(scope, stmt.name, it, stmt.generics))
      }

      val members = stmt.members.associate { (name, params) ->
        val funTy = FunTy(ty, params.ty().map(::checkTy))

        val variantScheme = if (params.isEmpty()) {
          scope.declare(name, scheme)
        } else {
          scope.declare(name, instantiate(funTy.generalize()).generalize())
        }

        val variantTy = instantiate(Scheme(variantScheme.names, ConstTy(name.text)))
        val memberInfo = scope.create(
          EnumMemberInfo(scope, name, variantTy, funTy, variantScheme),
        )

        name to memberInfo
      }
      val info = scope.create(EnumInfo(scope, stmt.name, ty, stmt.generics, members))

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
        parameters = stmt.parameters.keys.zip(parameters).toMap(),
      )
      val scope = FunctionScope(info, statements(stmt.body), scope, ModuleTree(scope.tree))
      val body = scoped(scope) {
        stmt.parameters.forEach { (name, type) ->
          declare(name, checkTy(type.ty()))
        }

        checkBody(stmt.body)
      }

      ResolvedFunDecl(body, stmt.attributes, scope.references, info, isNested, stmt.loc)
    }
  }
}
