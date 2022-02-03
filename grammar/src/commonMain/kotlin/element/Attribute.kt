package org.plank.grammar.element

data class Attribute(
  val name: Identifier,
  val arguments: List<AttributeExpr<*>>,
  override val location: Location,
) : PlankElement {
  inline fun <reified T : Any> argument(index: Int): T? {
    return arguments[index].value as? T
  }
}

sealed interface AttributeExpr<T> : PlankElement {
  val value: T
}

data class IntAttributeExpr(override val value: Int, override val location: Location) :
  AttributeExpr<Int>

data class StringAttributeExpr(override val value: String, override val location: Location) :
  AttributeExpr<String>

data class AccessAttributeExpr(override val value: Identifier, override val location: Location) :
  AttributeExpr<Identifier>

data class DecimalAttributeExpr(override val value: Double, override val location: Location) :
  AttributeExpr<Double>

data class BoolAttributeExpr(override val value: Boolean, override val location: Location) :
  AttributeExpr<Boolean>
