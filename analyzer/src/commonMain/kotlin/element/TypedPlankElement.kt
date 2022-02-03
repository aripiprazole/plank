package org.plank.analyzer.element

import org.plank.analyzer.PlankType

interface TypedPlankElement : ResolvedPlankElement {
  val type: PlankType
}
