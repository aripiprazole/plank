package org.plank.analyzer.infer

import kotlin.jvm.JvmInline

@JvmInline
value class Subst(private val map: Map<VarTy, Ty> = emptyMap()) {
  val types: Collection<Ty> get() = map.values

  constructor(name: String, ty: Ty) : this(mapOf(VarTy(name) to ty))

  constructor(builder: MutableMap<VarTy, Ty>.() -> Unit) : this(buildMap(builder))

  infix fun compose(other: Subst): Subst {
    return Subst((map + other.map).mapValues { it.value ap this })
  }

  operator fun get(name: String): Ty? = map[VarTy(name)]

  fun toMap(): Map<VarTy, Ty> = map

  override fun toString(): String =
    "Subst ${map.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}: ${it.value}" }}"
}

fun Map<VarTy, Ty>.toSubst(): Subst = Subst(this)

fun nullSubst(): Subst = Subst()

fun Scheme.ftv(): Set<String> {
  return ty.ftv().toMutableSet().also { it.removeAll(names) }
}

fun TyEnv.ftv(): Set<String> {
  return map.values.flatMap { it.ftv() }.toSet()
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
    is VarTy -> subst[name] ?: this
    is AppTy -> AppTy(fn ap subst, arg ap subst)
    is PtrTy -> PtrTy(arg ap subst)
    is FunTy -> FunTy(parameterTy ap subst, returnTy ap subst)
  }
}
