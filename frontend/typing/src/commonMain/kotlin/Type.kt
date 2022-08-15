package org.plank.typing

sealed interface Type {
  companion object {
    val Unit: Type = ConType("()")
    val Int32: Type = ConType("Int32")
    val Bool: Type = ConType("Bool")
    val String: Type = ConType("String")
    val Arrow: Type = ConType("->")

    fun variable(name: String): Type = VarType(name)
    fun constructor(name: String): Type = ConType(name)
  }
}

data class ConType(val name: String) : Type

data class VarType(val name: String) : Type

data class AppType(val lhs: Type, val rhs: Type) : Type

fun Type.ftv(): Set<String> = when (this) {
  is ConType -> emptySet()
  is VarType -> setOf(name)
  is AppType -> lhs.ftv() + rhs.ftv()
}

infix fun Type.apply(subst: Subst): Type {
  return when (this) {
    is AppType -> copy(lhs = lhs apply subst, rhs = rhs apply subst)
    is ConType -> this
    is VarType -> subst[name] ?: this
  }
}

infix fun Type.arrow(rhs: Type) = AppType(this, AppType(Type.Arrow, rhs))
