package org.plank.analyzer.checker

import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.chainParameters
import org.plank.analyzer.infer.doubleTy
import org.plank.analyzer.infer.floatTy
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.toIdentifier

sealed interface TyInfo {
  val name: Identifier
  val generics: Set<Identifier>
  val ty: Ty
  val declaredIn: Scope
}

data class FunctionInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: FunTy,
  val scheme: Scheme,
  override val generics: Set<Identifier> = emptySet(),
  val returnTy: Ty = ty.returnTy,
  val parameters: Map<Identifier, Ty> = emptyMap(),
  val isInline: Boolean = false,
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: Ty,
  override val generics: Set<Identifier> = emptySet(),
  val scheme: Scheme,
  val members: Map<Identifier, StructMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class StructMemberInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: Ty,
  val mutable: Boolean,
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = name.text
}

data class EnumInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: Ty,
  override val generics: Set<Identifier> = emptySet(),
  val scheme: Scheme,
  val members: Map<Identifier, EnumMemberInfo> = emptyMap(),
) : TyInfo {
  override fun toString(): String = name.text
}

data class EnumMemberInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: Ty,
  val scheme: Scheme,
  val funTy: FunTy,
  val subst: Subst,
  val parameters: List<Ty> = funTy.chainParameters(),
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = name.text
}

data class IntInfo(
  override val declaredIn: Scope,
  override val name: Identifier,
  override val ty: Ty,
  val size: Int,
  val unsigned: Boolean = false,
) : TyInfo {
  override val generics: Set<Identifier> = emptySet()

  constructor(declaredIn: Scope, name: String, ty: Ty, size: Int, unsigned: Boolean = false) :
    this(declaredIn, name.toIdentifier(), ty, size, unsigned)

  override fun toString(): String = "{${name.text}}"
}

class DoubleInfo(override val declaredIn: Scope) : TyInfo {
  override val name: Identifier = Identifier("Double")
  override val ty: Ty = doubleTy
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = "{double}"
}

class UnitInfo(override val declaredIn: Scope) : TyInfo {
  override val name: Identifier = Identifier("()")
  override val ty: Ty = unitTy
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = "{unit}"
}

class FloatInfo(override val declaredIn: Scope) : TyInfo {
  override val name: Identifier = Identifier("Float")
  override val ty: Ty = floatTy
  override val generics: Set<Identifier> = emptySet()

  override fun toString(): String = "{float}"
}

inline fun <reified A : TyInfo> TyInfo.getAs(): A? {
  return if (this is A) this else null
}
