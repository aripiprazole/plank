package com.gabrielleeg1.plank.analyzer.element

import com.gabrielleeg1.plank.analyzer.PlankType

interface TypedPlankElement : ResolvedPlankElement {
  val type: PlankType
}
