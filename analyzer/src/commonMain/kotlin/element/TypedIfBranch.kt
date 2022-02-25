package org.plank.analyzer.element

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedIfBranch : TypedPlankElement {
  interface Visitor<T> {
    fun visitIfBranch(branch: TypedIfBranch): T = branch.accept(this)
    fun visitThenBranch(branch: TypedThenBranch): T
    fun visitBlockBranch(branch: TypedBlockBranch): T
  }

  override fun ap(subst: Subst): TypedIfBranch

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedThenBranch(
  val value: TypedExpr,
  override val location: Location,
) : TypedIfBranch {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedThenBranch = copy(value = value.ap(subst))

  override fun <T> accept(visitor: TypedIfBranch.Visitor<T>): T {
    return visitor.visitThenBranch(this)
  }
}

data class TypedBlockBranch(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr,
  val references: MutableMap<Identifier, Ty> = mutableMapOf(),
  override val location: Location,
) : TypedIfBranch {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedBlockBranch = copy(value = value.ap(subst))

  override fun <T> accept(visitor: TypedIfBranch.Visitor<T>): T {
    return visitor.visitBlockBranch(this)
  }
}
