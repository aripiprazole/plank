package org.plank.typing

sealed interface Type {
  companion object {
    val Unit = ConType("()")
    val Int32 = ConType("Int32")
    val Bool = ConType("Bool")
    val String = ConType("String")
    val Arrow = ConType("->")
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
