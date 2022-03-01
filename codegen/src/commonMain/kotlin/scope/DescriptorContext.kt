package org.plank.codegen.scope

import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.infer.Subst
import org.plank.syntax.element.Loc

class DescriptorContext(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeContext,
  override val subst: Subst = enclosing.subst,
) : CodegenContext by enclosing {
  override val loc: Loc = descriptor.loc
}
