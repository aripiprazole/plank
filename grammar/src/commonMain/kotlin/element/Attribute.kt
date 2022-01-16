package com.gabrielleeg1.plank.grammar.element

data class Attribute(
  val name: Identifier,
  val arguments: List<AttributePrimary<*>>,
  override val location: Location,
) : PlankElement {
  inline fun <reified T : Any> argument(index: Int): T? {
    return arguments[index].value as? T
  }
}

sealed interface AttributePrimary<T> : PlankElement {
  val value: T
}

data class IntAttributePrimary(override val value: Int, override val location: Location) :
  AttributePrimary<Int>

data class StringAttributePrimary(override val value: String, override val location: Location) :
  AttributePrimary<String>

data class DecimalAttributePrimary(override val value: Double, override val location: Location) :
  AttributePrimary<Double>

data class BooleanAttributePrimary(override val value: Boolean, override val location: Location) :
  AttributePrimary<Boolean>
