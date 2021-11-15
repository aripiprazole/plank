package com.gabrielleeg1.plank.grammar.element

sealed class TypeRef : PlankElement {
  interface Visitor<T> {
    fun visit(typeDef: TypeRef): T = typeDef.accept(this)

    fun visitAccessTypeRef(ref: AccessTypeRef): T
    fun visitPointerTypeRef(ref: PointerTypeRef): T
    fun visitArrayTypeRef(ref: ArrayTypeRef): T
    fun visitFunctionTypeRef(ref: FunctionTypeRef): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T
}

data class AccessTypeRef(val path: QualifiedPath, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitAccessTypeRef(this)
  }
}

data class PointerTypeRef(val type: TypeRef, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitPointerTypeRef(this)
  }
}

data class ArrayTypeRef(val type: TypeRef, override val location: Location) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitArrayTypeRef(this)
  }
}

data class FunctionTypeRef(
  val parameters: List<TypeRef>,
  val returnType: TypeRef,
  override val location: Location
) : TypeRef() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitFunctionTypeRef(this)
  }
}
