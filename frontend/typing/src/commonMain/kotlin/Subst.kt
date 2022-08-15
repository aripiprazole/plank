package org.plank.typing

typealias Subst = Map<String, Type>

fun emptySubst(): Subst = emptyMap()

fun substOf(vararg pairs: Pair<String, Type>): Subst = mapOf(*pairs)

infix fun Subst.compose(other: Subst): Subst =
  plus(other).mapValues { (_, type) -> type apply this }
