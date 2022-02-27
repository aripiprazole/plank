package org.plank.analyzer.element

import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.i32Ty
import org.plank.analyzer.infer.nullSubst
import org.plank.analyzer.resolver.Scope
import org.plank.analyzer.resolver.StructInfo
import org.plank.analyzer.resolver.Variable
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location

sealed interface TypedExpr : TypedPlankElement {
  interface Visitor<T> {
    fun visitExpr(expr: TypedExpr): T = expr.accept(this)

    fun visitBlockExpr(expr: TypedBlockExpr): T
    fun visitConstExpr(expr: TypedConstExpr): T
    fun visitIfExpr(expr: TypedIfExpr): T
    fun visitAccessExpr(expr: TypedAccessExpr): T
    fun visitIntOperationExpr(expr: TypedIntOperationExpr): T
    fun visitCallExpr(expr: TypedCallExpr): T
    fun visitAssignExpr(expr: TypedAssignExpr): T
    fun visitSetExpr(expr: TypedSetExpr): T
    fun visitGetExpr(expr: TypedGetExpr): T
    fun visitGroupExpr(expr: TypedGroupExpr): T
    fun visitInstanceExpr(expr: TypedInstanceExpr): T
    fun visitSizeofExpr(expr: TypedSizeofExpr): T
    fun visitRefExpr(expr: TypedRefExpr): T
    fun visitDerefExpr(expr: TypedDerefExpr): T
    fun visitMatchExpr(expr: TypedMatchExpr): T
    fun visitEnumIndexAccess(expr: TypedEnumIndexAccess): T

    fun visitTypedExprs(many: List<TypedExpr>): List<T> = many.map(::visitExpr)
  }

  override val location: Location

  override infix fun ap(subst: Subst): TypedExpr

  fun <T> accept(visitor: Visitor<T>): T

  fun stmt(): ResolvedStmt = ResolvedExprStmt(this, location)
  fun body(): ResolvedFunctionBody = ResolvedExprBody(this, location)
}

data class TypedBlockExpr(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr,
  val references: MutableMap<Identifier, Ty> = mutableMapOf(),
  override val location: Location,
) : TypedExpr {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedBlockExpr = copy(value = value.ap(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitBlockExpr(this)
  }
}

data class TypedConstExpr(
  val value: Any,
  override val ty: Ty,
  override val subst: Subst = nullSubst(),
  override val location: Location = Location.Generated,
) : TypedExpr {
  override fun ap(subst: Subst): TypedConstExpr =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitConstExpr(this)
  }
}

data class TypedIfExpr(
  val cond: TypedExpr,
  val thenBranch: TypedIfBranch,
  val elseBranch: TypedIfBranch?,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedIfExpr = copy(
    cond = cond.ap(subst),
    thenBranch = thenBranch.ap(subst),
    elseBranch = elseBranch?.ap(subst),
    ty = ty.ap(subst),
    subst = subst.compose(subst),
  )

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitIfExpr(this)
  }
}

data class TypedAccessExpr(
  val variable: Variable,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  val scope: Scope = variable.declaredIn
  val name: Identifier = variable.name

  override fun ap(subst: Subst): TypedAccessExpr =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitAccessExpr(this)
  }
}

data class TypedGroupExpr(
  val value: TypedExpr,
  override val location: Location,
) : TypedExpr {
  override val ty: Ty = value.ty
  override val subst: Subst = value.subst

  override fun ap(subst: Subst): TypedGroupExpr = copy(value = value.ap(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitGroupExpr(this)
  }
}

data class TypedAssignExpr(
  val scope: Scope,
  val name: Identifier,
  val value: TypedExpr,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedAssignExpr =
    copy(value = value.ap(subst), ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitAssignExpr(this)
  }
}

data class TypedSetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val value: TypedExpr,
  val info: StructInfo,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedSetExpr =
    copy(
      receiver = receiver.ap(subst),
      value = value.ap(subst),
      ty = ty.ap(subst),
      subst = subst.compose(subst),
    )

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSetExpr(this)
  }
}

data class TypedGetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val info: StructInfo,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedGetExpr =
    copy(receiver = receiver.ap(subst), ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitGetExpr(this)
  }
}

sealed interface TypedIntOperationExpr : TypedExpr {
  val lhs: TypedExpr
  val rhs: TypedExpr
  val isConst: Boolean
  val unsigned: Boolean

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitIntOperationExpr(this)
  }
}

data class TypedIntAddExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty
  override val subst: Subst = lhs.subst compose rhs.subst

  override fun ap(subst: Subst): TypedIntAddExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntSubExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty
  override val subst: Subst = lhs.subst compose rhs.subst

  override fun ap(subst: Subst): TypedIntSubExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntMulExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty
  override val subst: Subst = lhs.subst compose rhs.subst

  override fun ap(subst: Subst): TypedIntMulExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntDivExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = i32Ty

  override fun ap(subst: Subst): TypedIntDivExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntEQExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntNEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntNEQExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntGTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntGTExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntGTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntGTEExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntLTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntLTExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedIntLTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
  override val subst: Subst = Subst(),
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntLTEExpr =
    copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst), subst = subst.compose(subst))
}

data class TypedCallExpr(
  val callee: TypedExpr,
  val argument: TypedExpr,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedCallExpr =
    copy(
      callee = callee.ap(subst),
      argument = argument.ap(subst),
      ty = ty.ap(subst),
      subst = subst.compose(subst),
    )

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitCallExpr(this)
  }
}

data class TypedInstanceExpr(
  val arguments: Map<Identifier, TypedExpr>,
  val info: StructInfo,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedInstanceExpr =
    copy(
      arguments = arguments.mapValues { it.value ap subst },
      ty = ty.ap(subst),
      subst = subst.compose(subst),
    )

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitInstanceExpr(this)
  }
}

data class TypedSizeofExpr(
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedSizeofExpr =
    copy(ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSizeofExpr(this)
  }
}

data class TypedRefExpr(
  val value: TypedExpr,
  override val location: Location,
) : TypedExpr {
  override val subst: Subst = value.subst
  override val ty: Ty = PtrTy(value.ty)

  override fun ap(subst: Subst): TypedRefExpr =
    copy(value = value.ap(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitRefExpr(this)
  }
}

data class TypedDerefExpr(
  val value: TypedExpr,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedDerefExpr =
    copy(value = value.ap(subst), ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitDerefExpr(this)
  }
}

data class TypedEnumIndexAccess(
  val value: TypedExpr,
  val index: Int,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedEnumIndexAccess =
    copy(value = value.ap(subst), ty = ty.ap(subst), subst = subst.compose(subst))

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitEnumIndexAccess(this)
  }
}

data class TypedMatchExpr(
  val subject: TypedExpr,
  val patterns: Map<TypedPattern, TypedExpr>,
  override val ty: Ty,
  override val subst: Subst,
  override val location: Location,
) : TypedExpr {
  override fun ap(subst: Subst): TypedMatchExpr =
    copy(
      subject = subject.ap(subst),
      patterns = patterns.mapKeys { it.key ap subst }.mapValues { it.value ap subst },
      ty = ty.ap(subst),
      subst = subst.compose(subst),
    )

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitMatchExpr(this)
  }
}
