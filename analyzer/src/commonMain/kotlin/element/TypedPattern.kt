package org.plank.analyzer.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.StructType
import org.plank.analyzer.Untyped
import org.plank.syntax.element.ErrorPlankElement
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed class TypedPattern : TypedPlankElement

data class TypedNamedTuplePattern(
  val properties: List<TypedPattern>,
  override val type: StructType,
  override val location: Location
) : TypedPattern()

data class TypedIdentPattern(
  val name: Identifier,
  override val type: PlankType,
  override val location: Location
) : TypedPattern()

data class ViolatedPattern(
  override val message: String,
  override val arguments: List<Any> = emptyList(),
  override val location: Location = Location.Generated,
) : TypedPattern(), ErrorPlankElement {
  override val type = Untyped
}
