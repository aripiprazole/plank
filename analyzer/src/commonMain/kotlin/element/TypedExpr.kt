package org.plank.analyzer.element

import org.plank.analyzer.BoolType
import org.plank.analyzer.FloatType
import org.plank.analyzer.Module
import org.plank.analyzer.PlankType
import org.plank.analyzer.PointerType
import org.plank.analyzer.StructType
import org.plank.analyzer.Untyped
import org.plank.analyzer.Variable
import org.plank.syntax.element.ErrorPlankElement
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
    fun visitViolatedExpr(expr: TypedErrorExpr): T

    fun visitTypedExprs(many: List<TypedExpr>): List<T> = many.map(::visitExpr)
  }

  override val location: Location

  fun <T> accept(visitor: Visitor<T>): T

  fun stmt(): ResolvedStmt = ResolvedExprStmt(this, location)
}

data class TypedBlockExpr(
  val stmts: List<ResolvedStmt>,
  val returned: TypedExpr,
  val references: LinkedHashMap<Identifier, PlankType> = LinkedHashMap(),
  override val type: PlankType,
  override val location: Location,
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitBlockExpr(this)
  }
}

data class TypedConstExpr(
  val value: Any,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitConstExpr(this)
  }
}

data class TypedIfExpr(
  val cond: TypedExpr,
  val thenBranch: TypedExpr,
  val elseBranch: TypedExpr?,
  override val type: PlankType,
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
  val name = variable.name
  override val type = variable.value.type

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitAccessExpr(this)
  }
}

data class TypedGroupExpr(val expr: TypedExpr, override val location: Location) : TypedExpr {
  override val type = expr.type

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitGroupExpr(this)
  }
}

data class TypedAssignExpr(
  val module: Module? = null,
  val name: Identifier,
  val value: TypedExpr,
  override val type: PlankType,
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
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSetExpr(this)
  }
}

data class TypedGetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  override val type: PlankType,
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

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitIntOperationExpr(this)
  }
}

data class TypedIntAddExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = rhs.type
}

data class TypedIntSubExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = rhs.type
}

data class TypedIntMulExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = rhs.type
}

data class TypedIntDivExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = FloatType()
}

data class TypedIntEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedIntNEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedIntGTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedIntGTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedIntLTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedIntLTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val location: Location = Location.Generated,
) : TypedIntOperationExpr {
  override val type: PlankType = BoolType
}

data class TypedCallExpr(
  val callee: TypedExpr,
  val arguments: List<TypedExpr>,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitCallExpr(this)
  }
}

data class TypedInstanceExpr(
  val arguments: Map<Identifier, TypedExpr>,
  override val type: StructType,
  override val location: Location,
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitInstanceExpr(this)
  }
}

data class TypedSizeofExpr(
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitSizeofExpr(this)
  }
}

data class TypedRefExpr(
  val expr: TypedExpr,
  override val location: Location
) : TypedExpr {
  override val type = PointerType(expr.type)

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitRefExpr(this)
  }
}

data class TypedDerefExpr(
  val expr: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitDerefExpr(this)
  }
}

data class TypedMatchExpr(
  val subject: TypedExpr,
  val patterns: Map<TypedPattern, TypedExpr>,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitMatchExpr(this)
  }
}

data class TypedErrorExpr(
  override val message: String,
  override val arguments: List<Any>,
  override val location: Location = Location.Generated,
) : TypedExpr, ErrorPlankElement {
  override val type = Untyped

  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitViolatedExpr(this)
  }
}
