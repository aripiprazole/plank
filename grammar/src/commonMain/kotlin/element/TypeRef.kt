package com.lorenzoog.plank.grammar.element

sealed class TypeRef : PlankElement {
  interface Visitor<T> {
    fun visit(typeDef: TypeRef): T = typeDef.accept(this)

    fun visitAccessTypeReference(ref: AccessTypeRef): T
    fun visitPointerTypeReference(ref: PointerTypeRef): T
    fun visitArrayTypeReference(ref: ArrayTypeRef): T
    fun visitFunctionTypeReference(ref: FunctionTypeRef): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class AccessTypeRef(val path: QualifiedPath, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAccessTypeReference(this)
  }
}

data class PointerTypeRef(val type: TypeRef, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitPointerTypeReference(this)
  }
}

data class ArrayTypeRef(val type: TypeRef, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitArrayTypeReference(this)
  }
}

data class FunctionTypeRef(
  val parameters: List<TypeRef>,
  val returnType: TypeRef,
  override val location: Location
) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitFunctionTypeReference(this)
  }
}
