package org.plank.syntax.element

sealed interface TypeRef : SimplePlankElement

data class UnitTypeRef(override val loc: Loc = GeneratedLoc) : TypeRef

data class GenericTypeRef(
  val name: Identifier,
  override val loc: Loc = GeneratedLoc,
) : TypeRef {
  constructor(name: String, loc: Loc = GeneratedLoc) : this(name.toIdentifier(), loc)
}

data class AccessTypeRef(
  val path: QualifiedPath,
  override val loc: Loc = GeneratedLoc,
) : TypeRef {
  constructor(name: String, loc: Loc = GeneratedLoc) : this(name.toQualifiedPath(), loc)
}

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
