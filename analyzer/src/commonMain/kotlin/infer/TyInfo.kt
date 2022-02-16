package org.plank.analyzer.infer

import org.plank.syntax.element.Identifier

sealed interface TyInfo {
  val name: Identifier
}

data class FunctionInfo(
  override val name: Identifier,
  val returnTy: Ty,
  val parameters: Map<Identifier, Ty>,
  val isInline: Boolean = false,
) : TyInfo

data class StructInfo(
  override val name: Identifier,
  val members: Map<Identifier, StructMemberInfo> = emptyMap(),
) : TyInfo

data class StructMemberInfo(
  override val name: Identifier,
  val ty: Ty,
  val mutable: Boolean,
) : TyInfo

data class EnumInfo(
  override val name: Identifier,
  val members: Map<Identifier, EnumMemberInfo> = emptyMap(),
) : TyInfo

data class EnumMemberInfo(
  override val name: Identifier,
  val parameters: List<Ty>,
  val funTy: Ty,
) : TyInfo

data class IntInfo(override val name: Identifier, val size: Int) : TyInfo {
  constructor(name: String, size: Int) : this(Identifier(name), size)

  override fun toString(): String = "i:int:$name"
}

object DoubleInfo : TyInfo {
  override val name: Identifier = Identifier("Double")

  override fun toString(): String = "i:Double"
}

object FloatInfo : TyInfo {
  override val name: Identifier = Identifier("Float")

  override fun toString(): String = "i:Float"
}

inline fun <reified A : TyInfo> TyInfo.getAs(): A? {
  return if (this is A) this else null
}
