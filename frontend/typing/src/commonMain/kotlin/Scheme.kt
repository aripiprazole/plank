package org.plank.typing

class Scheme private constructor(val variables: Set<String>, val type: Type) {
  companion object {
    fun forall(vararg variables: String): (Type) -> Scheme =
      fun(type: Type): Scheme = Scheme(variables.toSet(), type)
  }
}
