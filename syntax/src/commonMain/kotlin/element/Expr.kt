package org.plank.syntax.element

sealed interface Expr : SimplePlankElement

data class BlockExpr(
  val value: Expr? = null,
  val stmts: List<Stmt> = emptyList(),
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(vararg stmts: Stmt, value: Expr? = null, loc: Loc = GeneratedLoc) :
    this(value, stmts.toList(), loc)
}

data class MatchExpr(
  val subject: Expr,
  val patterns: Map<Pattern, Expr>,
  override val loc: Loc = GeneratedLoc,
) : Expr

data class IfExpr(
  val cond: Expr,
  val thenBranch: IfBranch,
  val elseBranch: IfBranch?,
  override val loc: Loc = GeneratedLoc,
) : Expr

data class ConstExpr(val value: Any, override val loc: Loc = GeneratedLoc) : Expr {
  override fun toString(): String {
    val string = when (value) {
      is Char -> "'$value'"
      is String -> "\"$value\""
      else -> value.toString()
    }

    return "ConstExpr(value=$string, loc=$loc)"
  }
}

data class AccessExpr(
  val name: Identifier,
  val module: QualifiedPath? = null,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(name: String, loc: Loc = GeneratedLoc) : this(
    name = name.toQualifiedPath().last(),
    module = name.toQualifiedPath().dropLast(),
    loc = loc,
  )
}

data class GroupExpr(val value: Expr, override val loc: Loc = GeneratedLoc) : Expr

data class AssignExpr(
  val name: Identifier,
  val value: Expr,
  val module: QualifiedPath? = null,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(name: String, value: Expr, loc: Loc = GeneratedLoc) : this(
    name = name.toQualifiedPath().last(),
    value = value,
    module = name.toQualifiedPath().dropLast(),
    loc = loc,
  )
}

data class SetExpr(
  val receiver: Expr,
  val property: Identifier,
  val value: Expr,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(receiver: Expr, property: String, value: Expr, loc: Loc = GeneratedLoc) :
    this(receiver, property.toIdentifier(), value, loc)
}

data class GetExpr(
  val receiver: Expr,
  val property: Identifier,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(receiver: Expr, property: String, loc: Loc = GeneratedLoc) :
    this(receiver, property.toIdentifier(), loc)
}

data class CallExpr(
  val callee: Expr,
  val arguments: List<Expr>,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(callee: Expr, vararg arguments: Expr, loc: Loc = GeneratedLoc) :
    this(callee, arguments.toList(), loc)
}

data class InstanceExpr(
  val type: TypeRef,
  val arguments: Map<Identifier, Expr>,
  override val loc: Loc = GeneratedLoc,
) : Expr {
  constructor(type: TypeRef, vararg arguments: Pair<String, Expr>, loc: Loc = GeneratedLoc) :
    this(type, arguments.toMap().mapKeys { it.key.toIdentifier() }, loc)
}

data class SizeofExpr(val type: TypeRef, override val loc: Loc = GeneratedLoc) :
  Expr

data class RefExpr(val value: Expr, override val loc: Loc = GeneratedLoc) : Expr

data class DerefExpr(val value: Expr, override val loc: Loc = GeneratedLoc) : Expr
