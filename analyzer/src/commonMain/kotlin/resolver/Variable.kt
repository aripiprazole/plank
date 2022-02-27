package org.plank.analyzer.resolver

import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier

sealed interface Variable {
  val mutable: Boolean
  val name: Identifier
  val ty: Ty
  val declaredIn: Scope
  val isInScope: Boolean

  fun name(name: Identifier): Variable

  fun inScope(): Variable
  fun notInScope(): Variable

  fun scheme(): Scheme {
    return when (this) {
      is InlineVariable -> Scheme(ty)
      is LocalVariable -> Scheme(ty)
      is RankedVariable -> scheme
    }
  }
}

data class RankedVariable(
  val scheme: Scheme,
  override val mutable: Boolean,
  override val name: Identifier,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
) : Variable {
  override val ty: Ty = scheme.ty

  override fun name(name: Identifier): RankedVariable = copy(name = name)
  override fun inScope(): RankedVariable = copy(isInScope = true)
  override fun notInScope(): RankedVariable = copy(isInScope = false)

  override fun toString(): String =
    "RankedVariable(mutable=$mutable, name=$name, scheme=$scheme, isInScope=$isInScope)"
}

data class LocalVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val ty: Ty,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
) : Variable {
  override fun name(name: Identifier): LocalVariable = copy(name = name)
  override fun inScope(): LocalVariable = copy(isInScope = true)
  override fun notInScope(): LocalVariable = copy(isInScope = false)

  override fun toString(): String =
    "LocalVariable(mutable=$mutable, name=$name, scheme=$ty, isInScope=$isInScope)"
}

data class InlineVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val ty: Ty,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
  val inlineCall: (List<TypedExpr>) -> ResolvedFunctionBody,
) : Variable {
  override fun name(name: Identifier): InlineVariable = copy(name = name)
  override fun inScope(): InlineVariable = copy(isInScope = true)
  override fun notInScope(): InlineVariable = copy(isInScope = false)

  override fun toString(): String =
    "InlineVariable(mutable=$mutable, name=$name, ty=$ty, isInScope=$isInScope)"
}
