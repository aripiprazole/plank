package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.toIdentifier
import org.plank.syntax.element.toQualifiedPath

fun TypeCheck.checkTy(ty: Ty): Ty = when (ty) {
  is VarTy -> ty
  is AppTy -> AppTy(checkTy(ty.fn), checkTy(ty.arg))
  is FunTy -> FunTy(checkTy(ty.parameterTy), checkTy(ty.returnTy))
  is PtrTy -> PtrTy(checkTy(ty.arg))
  is ConstTy -> lookupInfo(ty)?.let { ty }
    ?: violate<TypedExpr>(ConstExpr(Unit), UnresolvedType(ty)).ty
}

fun TypeCheck.lookupInfo(ty: Ty): TyInfo? = when (ty) {
  unitTy -> UnitInfo(GlobalScope)
  is AppTy -> lookupInfo(ty.fn)
  is VarTy -> scope.lookupTyInfo(ty.name.toIdentifier())
  is ConstTy -> {
    val path = QualifiedPath(ty.name)

    when (path.fullPath.size) {
      0 -> error("const path must have at least one component")
      1 -> scope.lookupTyInfo(ty.name.toIdentifier())
      else -> {
        val scope = scope.lookupModule(path.fullPath.dropLast(1).toQualifiedPath().toIdentifier())
          ?: scope

        scope.lookupTyInfo(path.fullPath.last())
      }
    }
  }
  else -> null
}
