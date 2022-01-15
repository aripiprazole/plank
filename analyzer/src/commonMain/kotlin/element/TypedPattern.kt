package com.gabrielleeg1.plank.analyzer.element

import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.Untyped
import com.gabrielleeg1.plank.grammar.element.ErrorPlankElement
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.Location

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
