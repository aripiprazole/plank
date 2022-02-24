package org.plank.analyzer.infer

import org.plank.syntax.element.Identifier
import org.plank.syntax.element.toIdentifier

sealed interface TyInfo {
  val name: Identifier
  val generics: Set<Identifier>
  val ty: Ty
}

data class FunctionInfo(
  override val name: Identifier,
  override val ty: Ty,
  override val generics: Set<Identifier>,
  val returnTy: Ty,
  val parameters: Map<Identifier, Ty>,
  val isInline: Boolean = false,
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructInfo(
  override val name: Identifier,
  override val ty: Ty,
  override val generics: Set<Identifier> = emptySet(),
  val members: Map<Identifier, StructMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructMemberInfo(
  override val name: Identifier,
  override val ty: Ty,
  val mutable: Boolean,
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = name.text
}

data class EnumInfo(
  override val name: Identifier,
  override val ty: Ty,
  override val generics: Set<Identifier> = emptySet(),
  val members: Map<Identifier, EnumMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class EnumMemberInfo(
  override val name: Identifier,
  override val ty: Ty,
  val parameters: List<Ty>,
  val funTy: FunTy,
  val scheme: Scheme,
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = name.text
}

data class IntInfo(
  override val name: Identifier,
  override val ty: Ty,
  val size: Int,
  val unsigned: Boolean = false,
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  constructor(name: String, ty: Ty, size: Int, unsigned: Boolean = false) :
    this(name.toIdentifier(), ty, size, unsigned)

  override fun toString(): String = "{${name.text}}"
}

object DoubleInfo : TyInfo {
  override val name: Identifier = Identifier("Double")
  override val ty: Ty = doubleTy
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = "{double}"
}

object FloatInfo : TyInfo {
  override val name: Identifier = Identifier("Float")
  override val ty: Ty = floatTy
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = "{float}"
}

inline fun <reified A : TyInfo> TyInfo.getAs(): A? {
  return if (this is A) this else null
}
