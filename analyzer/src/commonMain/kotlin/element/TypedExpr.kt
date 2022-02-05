package org.plank.analyzer.element

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
    fun visit(expr: TypedExpr): T = expr.accept(this)

    fun visitConstExpr(expr: TypedConstExpr): T
    fun visitIfExpr(expr: TypedIfExpr): T
    fun visitAccessExpr(expr: TypedAccessExpr): T
    fun visitCallExpr(expr: TypedCallExpr): T
    fun visitAssignExpr(expr: TypedAssignExpr): T
    fun visitModuleSetExpr(expr: TypedModuleSetExpr): T
    fun visitSetExpr(expr: TypedSetExpr): T
    fun visitModuleGetExpr(expr: TypedModuleGetExpr): T
    fun visitGetExpr(expr: TypedGetExpr): T
    fun visitGroupExpr(expr: TypedGroupExpr): T
    fun visitInstanceExpr(expr: TypedInstanceExpr): T
    fun visitSizeofExpr(expr: TypedSizeofExpr): T
    fun visitReferenceExpr(expr: TypedRefExpr): T
    fun visitDerefExpr(expr: TypedDerefExpr): T
    fun visitMatchExpr(expr: TypedMatchExpr): T
    fun visitViolatedExpr(expr: TypedErrorExpr): T

    fun visitTypedExprs(many: List<TypedExpr>): List<T> = many.map(::visit)
  }

  override val location: Location

  fun <T> accept(visitor: Visitor<T>): T

  fun stmt(): ResolvedStmt = ResolvedExprStmt(this, location)
}

data class TypedConstExpr(
  val value: Any,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  val literal = value.toString()

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

data class TypedAccessExpr(val variable: Variable, override val location: Location) : TypedExpr {
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
  val name: Identifier,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitAssignExpr(this)
  }
}

data class TypedModuleSetExpr(
  val module: Module,
  val member: Identifier,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitModuleSetExpr(this)
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

data class TypedModuleGetExpr(
  val module: Module,
  val member: Identifier,
  override val type: PlankType,
  override val location: Location
) : TypedExpr {
  override fun <T> accept(visitor: TypedExpr.Visitor<T>): T {
    return visitor.visitModuleGetExpr(this)
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
    return visitor.visitReferenceExpr(this)
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
