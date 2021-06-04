package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.PlankElement

interface TypedPlankElement : PlankElement {
  val type: PlankType get() = error("${this::class.simpleName} have no type")
}
