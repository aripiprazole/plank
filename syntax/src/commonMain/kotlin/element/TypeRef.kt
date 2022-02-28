package org.plank.syntax.element

sealed interface TypeRef : PlankElement

data class UnitTypeRef(override val location: Location = Location.Generated) : TypeRef

data class GenericTypeRef(
  val name: Identifier,
  override val location: Location = Location.Generated,
) : TypeRef

data class AccessTypeRef(
  val path: QualifiedPath,
  override val location: Location = Location.Generated,
) : TypeRef

data class PointerTypeRef(val type: TypeRef, override val location: Location = Location.Generated) :
  TypeRef

data class ApplyTypeRef(
  val function: TypeRef,
  val arguments: List<TypeRef>,
  override val location: Location,
) : TypeRef

data class FunctionTypeRef(
  val parameterType: TypeRef,
  val returnType: TypeRef,
  override val location: Location = Location.Generated,
) : TypeRef
