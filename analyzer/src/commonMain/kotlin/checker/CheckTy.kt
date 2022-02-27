package org.plank.analyzer.checker

import org.plank.analyzer.UnresolvedType
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.resolver.TyInfo
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.toIdentifier

fun TypeCheck.checkTy(ty: Ty): Ty = when (ty) {
  is AppTy -> AppTy(checkTy(ty.fn), checkTy(ty.arg))
  is FunTy -> FunTy(checkTy(ty.parameterTy), checkTy(ty.returnTy))
  is PtrTy -> PtrTy(checkTy(ty.arg))
  is VarTy -> lookupInfo(ty)?.let { ty }
    ?: violate<TypedExpr>(ConstExpr(Unit), UnresolvedType(ty)).ty
  is ConstTy -> lookupInfo(ty)?.let { ty }
    ?: violate<TypedExpr>(ConstExpr(Unit), UnresolvedType(ty)).ty
}

fun TypeCheck.lookupInfo(ty: Ty): TyInfo? = when (ty) {
  is AppTy -> lookupInfo(ty.fn)
  is ConstTy -> scope.findTyInfo(ty.name.toIdentifier())
  is VarTy -> scope.findTyInfo(ty.name.toIdentifier())
  else -> null
}
