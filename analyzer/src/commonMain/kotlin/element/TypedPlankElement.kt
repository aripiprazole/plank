package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty

interface TypedPlankElement : ResolvedPlankElement {
  val ty: Ty

  infix fun ap(subst: Subst): TypedPlankElement
}
