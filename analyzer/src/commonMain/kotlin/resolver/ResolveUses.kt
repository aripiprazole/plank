package org.plank.analyzer.resolver

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.arr
import org.plank.analyzer.infer.undefTy
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.Expr
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.GenericTypeRef
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UseDecl
import org.plank.syntax.element.transformTree

fun resolveUses(
  f: PlankFile,
  dependencies: MutableList<Module>,
  tree: ModuleTree,
  _scope: Scope,
): PlankFile {
  var currentScope = _scope

  fun enterDecl(decl: Stmt): Stmt {
    return when (decl) {
      is FunDecl -> decl.apply {
        val ty = undefTy arr undefTy

        val info = FunctionInfo(currentScope, name, ty, Scheme(ty))
        val scope = FunctionScope(info, statements(body), currentScope, tree = ModuleTree(tree))

        scope.declare(name, info.ty)

        currentScope = scope
      }
      is ModuleDecl -> decl.apply {
        val module = tree.findModule(decl.path.toIdentifier())
          ?: Module(decl.path.toIdentifier(), decl.content).apply {
            scope = ModuleScope(this, currentScope, ModuleTree(tree))

            currentScope.tree.createModule(this)
          }

        currentScope = module.scope
      }
      else -> decl
    }
  }

  fun exitDecl(decl: Stmt): Stmt {
    return when (decl) {
      is EnumDecl -> decl.apply {
        currentScope.create(EnumInfo(currentScope, name, undefTy))
      }

      is StructDecl -> decl.apply {
        currentScope.create(StructInfo(currentScope, name, undefTy))
      }

      is UseDecl -> decl.apply {
        val module = currentScope.findModule(path.toIdentifier()) ?: return decl

        currentScope.expand(module.scope)
      }

      is LetDecl -> decl.apply {
        currentScope.declare(name, undefTy)
      }

      is ModuleDecl,
      is FunDecl,
      -> decl.apply { currentScope = currentScope.enclosing!! }
      else -> decl
    }
  }

  fun exitTypeRef(ref: TypeRef): TypeRef {
    return when (ref) {
      is AccessTypeRef -> when {
        ref.path.fullPath.size > 1 -> {
          val name = ref.path.fullPath.last()
          val module = ref.path.copy(fullPath = ref.path.fullPath.dropLast(1))

          val scope = currentScope.findModule(module.toIdentifier())?.scope ?: return ref
          val info = scope.findTyInfo(name) ?: return ref

          ref.copy(path = info.declaredIn.fullPath() + name)
        }
        else -> {
          val name = ref.path.toIdentifier()

          val info = currentScope.findTyInfo(ref.path.toIdentifier())
            ?: return GenericTypeRef(name, ref.loc)

          ref.copy(path = info.declaredIn.fullPath() + name)
        }
      }
      else -> ref
    }
  }

  fun exitExpr(expr: Expr): Expr {
    return when (expr) {
      is GetExpr -> when (val receiver = expr.receiver) {
        is GetExpr -> {
          val chain = concatModule(receiver).asReversed().ifEmpty { return expr }
          val path = QualifiedPath(chain)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AccessExpr(name = expr.property, module = path, loc = expr.loc)
        }
        is AccessExpr -> {
          val fullPath = receiver.module?.fullPath.orEmpty().toTypedArray()
          val path = qualifiedPath(*fullPath, receiver.name)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AccessExpr(name = expr.property, module = path, loc = expr.loc)
        }
        else -> expr
      }
      is SetExpr -> when (val receiver = expr.receiver) {
        is GetExpr -> {
          val chain = concatModule(receiver).asReversed().ifEmpty { return expr }
          val path = QualifiedPath(chain)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AssignExpr(name = expr.property, value = expr.value, module = path, loc = expr.loc)
        }
        is AccessExpr -> {
          val fullPath = receiver.module?.fullPath.orEmpty().toTypedArray()
          val path = qualifiedPath(*fullPath, receiver.name)

          dependencies.add(tree.findModule(path.toIdentifier()) ?: return expr)

          AssignExpr(name = expr.property, value = expr.value, module = path, loc = expr.loc)
        }
        else -> expr
      }
      is AccessExpr -> {
        val variable = currentScope.lookupVariable(expr.name) ?: return expr

        expr.copy(module = variable.declaredIn.fullPath())
      }
      else -> expr
    }
  }

  return transformTree(
    f,
    exitTypeRef = ::exitTypeRef,
    exitExpr = ::exitExpr,
    enterStmt = ::enterDecl,
    exitStmt = ::exitDecl,
  )
}
