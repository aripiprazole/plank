package org.plank.syntax.element

sealed interface TypeRef : PlankElement {
  interface Visitor<T> {
    fun visitTypeRef(ref: TypeRef): T = ref.accept(this)

    fun visitAccessTypeRef(ref: AccessTypeRef): T
    fun visitPointerTypeRef(ref: PointerTypeRef): T
    fun visitApplyTypeRef(ref: ApplyTypeRef): T
    fun visitFunctionTypeRef(ref: FunctionTypeRef): T
    fun visitUnitTypeRef(ref: UnitTypeRef): T

    fun visitTypeRefs(many: List<TypeRef>): List<T> = many.map(::visitTypeRef)
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

data class ApplyTypeRef(
  val function: QualifiedPath,
  val arguments: List<TypeRef>,
  override val location: Location,
) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitApplyTypeRef(this)
  }
}

data class FunctionTypeRef(
  val parameterType: TypeRef,
  val returnType: TypeRef,
  override val location: Location,
) : TypeRef {
  override fun <T> accept(visitor: TypeRef.Visitor<T>): T {
    return visitor.visitFunctionTypeRef(this)
  }
}
