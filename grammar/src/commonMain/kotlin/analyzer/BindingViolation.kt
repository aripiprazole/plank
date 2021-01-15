package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType
import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.message.MessageRenderer

sealed class BindingViolation {
  abstract val location: Location

  abstract fun render(renderer: MessageRenderer)
}

data class TypeViolation(
  val expected: Any,
  val actual: PkType,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe("Expected $expected but got $actual", location)
  }
}

data class AssignImmutableViolation(
  val name: String,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe("Variable $name is not mutable", location)
  }
}

data class UnexpectedGenericArgument(
  val expected: Int,
  val actual: Int,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    if (expected == 0) {
      return renderer.severe("Unexpected generic arguments", location)
    }

    renderer.severe("Unexpected $actual generic arguments, expected $expected", location)
  }
}

data class UnresolvedTypeViolation(
  val type: String,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe("Unresolved type $type", location)
  }
}

data class UnresolvedVariableViolation(
  val name: String,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe("Unresolved variable $name", location)
  }
}

data class UnresolvedModuleViolation(
  val name: String,
  override val location: Location
) : BindingViolation() {
  override fun render(renderer: MessageRenderer) {
    renderer.severe("Unresolved module $name", location)
  }
}

fun List<BindingViolation>.render(renderer: MessageRenderer) {
  forEach { it.render(renderer) }
}
