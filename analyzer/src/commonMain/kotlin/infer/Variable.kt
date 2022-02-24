package org.plank.analyzer.infer

import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.TypedExpr
import org.plank.syntax.element.Identifier

sealed interface Variable {
  val mutable: Boolean
  val name: Identifier
  val scheme: Scheme
  val declaredIn: Scope
  val isInScope: Boolean

  fun name(name: Identifier): Variable

  fun inScope(): Variable
  fun notInScope(): Variable
}

data class SimpleVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val scheme: Scheme,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
) : Variable {
  override fun name(name: Identifier): SimpleVariable = copy(name = name)
  override fun inScope(): SimpleVariable = copy(isInScope = true)
  override fun notInScope(): SimpleVariable = copy(isInScope = false)

  override fun toString(): String =
    "SimpleVariable(mutable=$mutable, name=$name, scheme=$scheme, isInScope=$isInScope)"
}

data class InlineVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val scheme: Scheme,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
  val inlineCall: (List<TypedExpr>) -> ResolvedFunctionBody,
) : Variable {
  override fun name(name: Identifier): InlineVariable = copy(name = name)
  override fun inScope(): InlineVariable = copy(isInScope = true)
  override fun notInScope(): InlineVariable = copy(isInScope = false)

  override fun toString(): String =
    "InlineVariable(mutable=$mutable, name=$name, scheme=$scheme, isInScope=$isInScope)"
}
