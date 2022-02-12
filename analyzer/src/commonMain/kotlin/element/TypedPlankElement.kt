package org.plank.analyzer.element

import org.plank.analyzer.Mono

interface TypedPlankElement : ResolvedPlankElement {
  val type: Mono
}
