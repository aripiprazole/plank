package org.plank.analyzer.infer

import kotlin.jvm.JvmInline

@JvmInline
value class Subst(val map: Map<VarTy, Ty> = emptyMap()) {
  constructor(name: String, ty: Ty) : this(mapOf(VarTy(name) to ty))
}

fun Ty.ftv(): Set<String> {
  return when (this) {
    is ConstTy -> emptySet()
    is VarTy -> setOf(name)
    is AppTy -> fn.ftv() + arg.ftv()
    is PtrTy -> arg.ftv()
    is FunTy -> returnTy.ftv() + parameterTy.ftv()
  }
}

infix fun Ty.ap(subst: Subst): Ty {
  return when (this) {
    is ConstTy -> this
    is VarTy -> subst.map[this] ?: this
    is AppTy -> AppTy(fn ap subst, arg ap subst)
    is PtrTy -> PtrTy(arg ap subst)
    is FunTy -> FunTy(returnTy ap subst, parameterTy ap subst)
  }
}
