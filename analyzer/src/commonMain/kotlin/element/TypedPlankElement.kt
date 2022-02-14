package org.plank.analyzer.element

import org.plank.analyzer.Ty

interface TypedPlankElement : ResolvedPlankElement {
  val type: Ty
}
