package org.plank.typing

sealed interface Type {
  companion object {
    val Unit: Hole<Type> = ConType("()").asHole()
    val Int32: Hole<Type> = ConType("Int32").asHole()
    val Bool: Hole<Type> = ConType("Bool").asHole()
    val String: Hole<Type> = ConType("String").asHole()
    val Arrow: Hole<Type> = ConType("->").asHole()

    fun variable(name: String): Type = VarType(name)
    fun constructor(name: String): Type = ConType(name)
  }
}

data class ConType(val name: String) : Type {
  override fun toString(): String = name
}

data class VarType(val name: String) : Type {
  override fun toString(): String = "'$name"
}

data class AppType(val lhs: Hole<Type>, val rhs: Hole<Type>) : Type {
  override fun toString(): String = "${lhs.unwrap()} ${rhs.unwrap()}"
}

fun Type.ftv(): Set<String> = when (this) {
  is ConType -> emptySet()
  is VarType -> setOf(name)
  is AppType -> lhs.unwrap().ftv() + rhs.unwrap().ftv()
}

infix fun Hole<Type>.arrow(rhs: Hole<Type>): Hole<Type> = AppType(
  lhs = this,
  rhs = AppType(
    lhs = Type.Arrow,
    rhs = rhs,
  ).asHole(),
).asHole()

infix fun Type.arrow(rhs: Type): Hole<Type> {
  val lhsHole = asHole()
  val rhsHole = if (rhs == this) lhsHole else rhs.asHole()
  
  return lhsHole.arrow(rhsHole)
}
