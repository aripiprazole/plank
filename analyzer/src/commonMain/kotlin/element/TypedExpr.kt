package org.plank.analyzer.element

import org.plank.analyzer.infer.Module
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.StructInfo
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.Variable
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.i32Ty
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

    fun visitTypedExprs(many: List<TypedExpr>): List<T> = many.map(::visitExpr)
  }

  override val location: Location

  fun <T> accept(visitor: Visitor<T>): T

  fun stmt(): ResolvedStmt = ResolvedExprStmt(this, location)
}

data class TypedBlockExpr(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr,
  val references: MutableMap<Identifier, Ty> = mutableMapOf(),
  override val ty: Ty,
  override val location: Location,
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitBlockExpr(this)
  }
}

data class TypedConstExpr(
  val value: Any,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitConstExpr(this)
  }
}

data class TypedIfExpr(
  val cond: TypedExpr,
  val thenBranch: TypedIfBranch,
  val elseBranch: TypedIfBranch?,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitIfExpr(this)
  }
}

data class TypedAccessExpr(
  val module: Module? = null,
  val variable: Variable,
  override val location: Location,
) : TypedExpr {
  val name: Identifier = variable.name
  override val ty: Ty = variable.ty

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitAccessExpr(this)
  }
}

data class TypedGroupExpr(val value: TypedExpr, override val location: Location) : TypedExpr {
  override val ty = value.ty

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitGroupExpr(this)
  }
}

data class TypedAssignExpr(
  val module: Module? = null,
  val name: Identifier,
  val value: TypedExpr,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
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
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSetExpr(this)
  }
}

data class TypedGetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val info: StructInfo,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
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
}

data class TypedIntSubExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty
}

data class TypedIntMulExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty
}

data class TypedIntDivExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = i32Ty
}

data class TypedIntEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedIntNEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedIntGTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedIntGTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedIntLTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedIntLTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy
}

data class TypedCallExpr(
  val callee: TypedExpr,
  val argument: TypedExpr,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitCallExpr(this)
  }
}

data class TypedInstanceExpr(
  val arguments: Map<Identifier, TypedExpr>,
  val info: StructInfo,
  override val ty: Ty,
  override val location: Location,
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitInstanceExpr(this)
  }
}

data class TypedSizeofExpr(
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSizeofExpr(this)
  }
}

data class TypedRefExpr(
  val value: TypedExpr,
  override val location: Location
) : TypedExpr {
  override val ty: Ty = PtrTy(value.ty)

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitRefExpr(this)
  }
}

data class TypedDerefExpr(
  val value: TypedExpr,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitDerefExpr(this)
  }
}

data class TypedMatchExpr(
  val subject: TypedExpr,
  val patterns: Map<TypedPattern, TypedExpr>,
  override val ty: Ty,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitMatchExpr(this)
  }
}
