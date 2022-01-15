package com.gabrielleeg1.plank.grammar.element

sealed interface TypeRef : PlankElement

data class AccessTypeRef(val path: QualifiedPath, override val location: Location) : TypeRef

data class PointerTypeRef(val type: TypeRef, override val location: Location) : TypeRef

data class ArrayTypeRef(val type: TypeRef, override val location: Location) : TypeRef

data class FunctionTypeRef(
  val parameters: List<TypeRef>,
  val returnType: TypeRef,
  override val location: Location
) : TypeRef
