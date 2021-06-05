package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.EnumType
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

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
