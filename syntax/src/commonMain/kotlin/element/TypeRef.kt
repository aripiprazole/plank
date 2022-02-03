package org.plank.syntax.element

sealed interface TypeRef : PlankElement {
  interface Visitor<T> {
    fun visit(ref: TypeRef): T = ref.accept(this)

    fun visitAccessTypeRef(ref: AccessTypeRef): T
    fun visitPointerTypeRef(ref: PointerTypeRef): T
    fun visitArrayTypeRef(ref: ArrayTypeRef): T
    fun visitFunctionTypeRef(ref: FunctionTypeRef): T
    fun visitUnitTypeRef(ref: UnitTypeRef): T

    fun visitTypeRefs(many: List<TypeRef>): List<T> = many.map(::visit)
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class UnitTypeRef(override val location: Location = Location.Generated) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitUnitTypeRef(this)
  }
}

data class AccessTypeRef(val path: QualifiedPath, override val location: Location) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitAccessTypeRef(this)
  }
}

data class PointerTypeRef(val type: TypeRef, override val location: Location) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitPointerTypeRef(this)
  }
}

data class ArrayTypeRef(val type: TypeRef, override val location: Location) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitArrayTypeRef(this)
  }
}

data class FunctionTypeRef(
  val parameter: TypeRef?,
  val returnType: TypeRef,
  val actualReturnType: TypeRef = returnType,
  val realParameters: Map<Identifier, TypeRef> = emptyMap(),
  val isClosure: Boolean? = false,
  override val location: Location,
) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitFunctionTypeRef(this)
  }
}
