package org.plank.analyzer.element

import org.plank.analyzer.EnumType
import org.plank.analyzer.PlankType
import org.plank.analyzer.Untyped
import org.plank.grammar.element.ErrorPlankElement
import org.plank.grammar.element.Identifier
import org.plank.grammar.element.Location

sealed class TypedPattern : TypedPlankElement

data class TypedNamedTuplePattern(
  val properties: List<TypedPattern>,
  override val type: EnumType,
  override val location: Location
) : TypedPattern()

data class TypedIdentPattern(
  val name: Identifier,
  override val type: PlankType,
  override val location: Location
) : TypedPattern()

data class ViolatedPattern(
  override val message: String,
  override val arguments: List<Any>
) : TypedPattern(), ErrorPlankElement {
  override val location = Location.Generated
  override val type = Untyped
}
