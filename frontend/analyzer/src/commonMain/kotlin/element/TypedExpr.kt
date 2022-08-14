package org.plank.analyzer.element

import org.plank.analyzer.infer.Scope
import org.plank.analyzer.infer.StructInfo
import org.plank.analyzer.infer.Variable
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.i32Ty
import org.plank.analyzer.infer.nullSubst
import org.plank.analyzer.infer.unify
import org.plank.syntax.element.GeneratedLoc
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc

sealed interface TypedExpr : TypedPlankElement {
  override val loc: Loc

  override infix fun ap(subst: Subst): TypedExpr
}

data class TypedBlockExpr(
  val stmts: List<ResolvedStmt>,
  val value: TypedExpr,
  val references: MutableMap<Identifier, Ty> = mutableMapOf(),
  override val loc: Loc,
) : TypedExpr {
  override val ty: Ty = value.ty

  override fun ap(subst: Subst): TypedBlockExpr = copy(value = value.ap(subst))
}

data class TypedConstExpr(
  val value: Any,
  override val ty: Ty,
  override val loc: Loc = GeneratedLoc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedConstExpr = copy(ty = ty.ap(subst))
}

data class TypedIfExpr(
  val cond: TypedExpr,
  val thenBranch: TypedIfBranch,
  val elseBranch: TypedIfBranch?,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedIfExpr = copy(
    cond = cond.ap(subst),
    thenBranch = thenBranch.ap(subst),
    elseBranch = elseBranch?.ap(subst),
    ty = ty.ap(subst),
  )
}

data class TypedAccessExpr(
  val variable: Variable,
  val subst: Subst,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  val scope: Scope = variable.declaredIn
  val name: Identifier = variable.name

  override fun ap(subst: Subst): TypedAccessExpr {
    val realSubst =
      runCatching { unify(variable.scheme.ty, ty ap subst) }
        .getOrDefault(nullSubst())

    return copy(ty = ty.ap(subst), subst = realSubst)
  }
}

data class TypedGroupExpr(
  val value: TypedExpr,
  override val loc: Loc,
) : TypedExpr {
  override val ty: Ty = value.ty

  override fun ap(subst: Subst): TypedGroupExpr = copy(value = value.ap(subst))
}

data class TypedAssignExpr(
  val scope: Scope,
  val name: Identifier,
  val value: TypedExpr,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedAssignExpr = copy(value = value.ap(subst), ty = ty.ap(subst))
}

data class TypedSetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val value: TypedExpr,
  val info: StructInfo,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedSetExpr = copy(
    receiver = receiver.ap(subst),
    value = value.ap(subst),
    ty = ty.ap(subst),
  )
}

data class TypedGetExpr(
  val receiver: TypedExpr,
  val member: Identifier,
  val info: StructInfo,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedGetExpr =
    copy(receiver = receiver.ap(subst), ty = ty.ap(subst))
}

sealed interface TypedIntOperationExpr : TypedExpr {
  val lhs: TypedExpr
  val rhs: TypedExpr
  val isConst: Boolean
  val unsigned: Boolean
}

data class TypedIntAddExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty

  override fun ap(subst: Subst): TypedIntAddExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntSubExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty

  override fun ap(subst: Subst): TypedIntSubExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntMulExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = rhs.ty

  override fun ap(subst: Subst): TypedIntMulExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntDivExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = i32Ty

  override fun ap(subst: Subst): TypedIntDivExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntEQExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntNEQExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntNEQExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntGTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntGTExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntGTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntGTEExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntLTExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntLTExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedIntLTEExpr(
  override val lhs: TypedExpr,
  override val rhs: TypedExpr,
  override val isConst: Boolean = false,
  override val unsigned: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : TypedIntOperationExpr {
  override val ty: Ty = boolTy

  override fun ap(subst: Subst): TypedIntLTEExpr = copy(lhs = lhs.ap(subst), rhs = rhs.ap(subst))
}

data class TypedCallExpr(
  val callee: TypedExpr,
  val argument: TypedExpr,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedCallExpr = copy(
    callee = callee.ap(subst),
    argument = argument.ap(subst),
    ty = ty.ap(subst),
  )
}

data class TypedInstanceExpr(
  val arguments: Map<Identifier, TypedExpr>,
  val info: StructInfo,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedInstanceExpr =
    copy(
      arguments = arguments.mapValues { it.value ap subst },
      ty = ty.ap(subst),
    )
}

data class TypedSizeofExpr(
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedSizeofExpr =
    copy(ty = ty.ap(subst))
}

data class TypedRefExpr(
  val value: TypedExpr,
  override val loc: Loc,
) : TypedExpr {
  override val ty: Ty = PtrTy(value.ty)

  override fun ap(subst: Subst): TypedRefExpr =
    copy(value = value.ap(subst))
}

data class TypedDerefExpr(
  val value: TypedExpr,
  override val ty: Ty,

  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedDerefExpr =
    copy(value = value.ap(subst), ty = ty.ap(subst))
}

data class TypedEnumIndexAccess(
  val value: TypedExpr,
  val index: Int,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedEnumIndexAccess =
    copy(value = value.ap(subst), ty = ty.ap(subst))
}

data class TypedMatchExpr(
  val subject: TypedExpr,
  val patterns: Map<TypedPattern, TypedExpr>,
  override val ty: Ty,
  override val loc: Loc,
) : TypedExpr {
  override fun ap(subst: Subst): TypedMatchExpr =
    copy(
      subject = subject.ap(subst),
      patterns = patterns.mapKeys { it.key ap subst }.mapValues { it.value ap subst },
      ty = ty.ap(subst),
    )
}
