package org.plank.analyzer.element

import org.plank.analyzer.infer.Ty

interface TypedPlankElement : ResolvedPlankElement {
  val ty: Ty
}
