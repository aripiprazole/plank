package org.plank.analyzer.infer

fun unify(a: Ty, b: Ty): Subst = when {
  a == b -> Subst()
  a is VarTy -> a bind b
  b is VarTy -> b bind a
  a is AppTy && b is AppTy -> {
    val s1 = unify(a.fn, b.fn)
    val s2 = unify(a.arg ap s1, b.arg ap s1)

    s1 compose s2
  }
  a is FunTy && b is FunTy -> {
    val s1 = unify(a.returnTy, b.returnTy)
    val s2 = unify(a.parameterTy ap s1, b.parameterTy ap s1)

    s1 compose s2
  }
  a is PtrTy && b is PtrTy -> unify(a.arg, b.arg)
  else -> throw UnificationFail(a, b)
}

infix fun VarTy.bind(other: Ty): Subst = when {
  this == other -> Subst()
  name in other.ftv() -> throw InfiniteTy(this, other)
  else -> Subst(name, other)
}
