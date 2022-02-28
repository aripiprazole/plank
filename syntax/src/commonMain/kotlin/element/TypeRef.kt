package org.plank.syntax.element

sealed interface TypeRef : SimplePlankElement

data class UnitTypeRef(override val loc: Loc = GeneratedLoc) : TypeRef

data class GenericTypeRef(
  val name: Identifier,
  override val loc: Loc = GeneratedLoc,
) : TypeRef

data class AccessTypeRef(
  val path: QualifiedPath,
  override val loc: Loc = GeneratedLoc,
) : TypeRef

data class PointerTypeRef(val type: TypeRef, override val loc: Loc = GeneratedLoc) :
  TypeRef

data class ApplyTypeRef(
  val function: TypeRef,
  val arguments: List<TypeRef>,
  override val loc: Loc,
) : TypeRef

data class FunctionTypeRef(
  val parameterType: TypeRef,
  val returnType: TypeRef,
  override val loc: Loc = GeneratedLoc,
) : TypeRef
