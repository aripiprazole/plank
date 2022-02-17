package org.plank.analyzer.element

import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedIfBranch : TypedPlankElement {
  interface Visitor<T> {
    fun visitIfBranch(branch: TypedIfBranch): T = branch.accept(this)
    fun visitThenBranch(branch: TypedThenBranch): T
    fun visitBlockBranch(branch: TypedBlockBranch): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class TypedThenBranch(val value: TypedExpr, override val location: Location) : TypedIfBranch {
  override val ty: Ty = value.ty

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

  override fun <T> accept(visitor: TypedIfBranch.Visitor<T>): T {
    return visitor.visitBlockBranch(this)
  }
}
