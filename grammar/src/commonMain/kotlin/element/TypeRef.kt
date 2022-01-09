package com.gabrielleeg1.plank.grammar.element

sealed interface TypeRef : PlankElement {
  interface Visitor<T> {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Replace with pattern matching")
    fun visit(typeDef: TypeRef): T = when (typeDef) {
      is AccessTypeRef -> visitAccessTypeRef(typeDef)
      is PointerTypeRef -> visitPointerTypeRef(typeDef)
      is ArrayTypeRef -> visitArrayTypeRef(typeDef)
      is FunctionTypeRef -> visitFunctionTypeRef(typeDef)
    }

    fun visitAccessTypeRef(ref: AccessTypeRef): T
    fun visitPointerTypeRef(ref: PointerTypeRef): T
    fun visitArrayTypeRef(ref: ArrayTypeRef): T
    fun visitFunctionTypeRef(ref: FunctionTypeRef): T
  }
}

data class AccessTypeRef(val path: QualifiedPath, override val location: Location) : TypeRef

data class PointerTypeRef(val type: TypeRef, override val location: Location) : TypeRef

data class ArrayTypeRef(val type: TypeRef, override val location: Location) : TypeRef

data class FunctionTypeRef(
  val parameters: List<TypeRef>,
  val returnType: TypeRef,
  override val location: Location
) : TypeRef
