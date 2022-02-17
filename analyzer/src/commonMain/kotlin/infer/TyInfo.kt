package org.plank.analyzer.infer

import org.plank.syntax.element.Identifier
import org.plank.syntax.element.toIdentifier

sealed interface TyInfo {
  val name: Identifier
}

data class FunctionInfo(
  override val name: Identifier,
  val returnTy: Ty,
  val parameters: Map<Identifier, Ty>,
  val isInline: Boolean = false,
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructInfo(
  override val name: Identifier,
  val members: Map<Identifier, StructMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructMemberInfo(
  override val name: Identifier,
  val ty: Ty,
  val mutable: Boolean,
) : TyInfo {
  override fun toString(): String = name.text
}

data class EnumInfo(
  override val name: Identifier,
  val members: Map<Identifier, EnumMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class EnumMemberInfo(
  override val name: Identifier,
  val parameters: List<Ty>,
  val funTy: Ty,
) : TyInfo {
  override fun toString(): String = name.text
}

data class IntInfo(override val name: Identifier, val size: Int, val unsigned: Boolean = false) :
  TyInfo {
  constructor(name: String, size: Int, unsigned: Boolean = false) :
    this(name.toIdentifier(), size, unsigned)

  override fun toString(): String = "{$name}"
}

object DoubleInfo : TyInfo {
  override val name: Identifier = Identifier("Double")

  override fun toString(): String = "{double}"
}

object FloatInfo : TyInfo {
  override val name: Identifier = Identifier("Float")

  override fun toString(): String = "{float}"
}

inline fun <reified A : TyInfo> TyInfo.getAs(): A? {
  return if (this is A) this else null
}
