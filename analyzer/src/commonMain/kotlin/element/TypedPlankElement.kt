package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType

interface TypedPlankElement : ResolvedPlankElement {
  val type: PlankType
}
