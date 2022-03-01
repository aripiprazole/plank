package org.plank.codegen.scope

import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.pretty
import org.plank.syntax.element.Loc

class DescriptorCtx(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeCtx,
  override val subst: Subst = enclosing.subst,
) : CodegenCtx by enclosing {
  override val loc: Loc = descriptor.loc

  override fun toString(): String = "DescriptorCtx(${descriptor.pretty()}, $subst) <: $enclosing"
}
