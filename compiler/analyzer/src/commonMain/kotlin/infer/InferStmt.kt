package org.plank.analyzer.infer

import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.UnitTypeRef

fun Infer.inferStmts(
  env: TyEnv,
  stmts: Collection<Stmt>,
  module: QualifiedPath = QualifiedPath(),
): TyEnv {
  return stmts.fold(env) { acc, stmt -> inferStmt(acc, stmt, module) }
}

fun Infer.inferStmt(env: TyEnv, stmt: Stmt, module: QualifiedPath = QualifiedPath()): TyEnv =
  when (stmt) {
    is ModuleDecl -> inferStmts(env, stmt.content, module + stmt.path)
    is EnumDecl -> {
      val enumTy = stmt.generics.fold(ConstTy((module + stmt.name).text) as Ty) { acc, next ->
        AppTy(acc, VarTy(next.text))
      }

      stmt.members.fold(env) { acc, next ->
        val name = module + next.name
        when {
          next.parameters.isEmpty() -> acc.extend(name.text, acc.generalize(enumTy))
          else -> {
            val ty = FunTy(enumTy, next.parameters.ty())
            acc.extend(name.text, acc.generalize(ty))
          }
        }
      }
    }

    is LetDecl -> {
      val tv = fresh()
      val (t1, s1) = inferExpr(env, stmt.value)
      val t2 = stmt.type?.ty() ?: t1
      val s2 = unify(t2 ap s1, tv)

      env.extend((module + stmt.name).text, env.generalize(tv ap s2))
    }

    is FunDecl -> {
      val cache = mutableMapOf<String, Ty>()
      val tv = fresh()

      val parameters = stmt.parameters.values.ifEmpty { listOf(UnitTypeRef()) }.map {
        when (val ty = it.ty()) {
          is VarTy -> cache.getOrPut(ty.name) { fresh() }
          else -> ty
        }
      }
      val returnTy = stmt.returnType.ty()

      val s1 = unify(FunTy(returnTy, parameters), tv)

      env.extend((module + stmt.name).text, env.generalize(tv ap s1)).also {
        inferFunctionBody(it, stmt.body)
      }
    }

    else -> env
  }
