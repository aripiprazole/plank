package org.plank.syntax.element

data class Attribute(
  val name: Identifier,
  val arguments: List<AttributeExpr<*>>,
  override val loc: Loc,
) : PlankElement {
  inline fun <reified T : Any> argument(index: Int): T? {
    return arguments[index].value as? T
  }
}

sealed interface AttributeExpr<T> : PlankElement {
  val value: T
}

data class IntAttributeExpr(override val value: Int, override val loc: Loc) :
  AttributeExpr<Int>

data class StringAttributeExpr(override val value: String, override val loc: Loc) :
  AttributeExpr<String>

data class AccessAttributeExpr(override val value: Identifier, override val loc: Loc) :
  AttributeExpr<Identifier>

data class DecimalAttributeExpr(override val value: Double, override val loc: Loc) :
  AttributeExpr<Double>

data class BoolAttributeExpr(override val value: Boolean, override val loc: Loc) :
  AttributeExpr<Boolean>
