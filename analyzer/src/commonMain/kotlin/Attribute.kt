package com.gabrielleeg1.plank.analyzer

/**
 * Represents an attribute to function/struct/enum.
 *
 * E.G in pseudo-code:
 *
 * ```reasonml
 * // the derive attribute will be handled by the compiler and will create
 * // an Show instance(in ad-hoc polymorphism) for List.
 * @derive(Show)
 * type List =
 *   | Cons(String, List)
 *   | Nil;
 * ```
 */
data class Attribute(val name: String) {
  companion object {
    val native = Attribute("native")
  }
}
