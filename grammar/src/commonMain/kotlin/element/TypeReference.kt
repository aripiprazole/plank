package com.lorenzoog.plank.grammar.element

sealed class TypeReference : PlankElement {
  interface Visitor<T> {
    fun visit(typeDef: TypeReference): T = typeDef.accept(this)

    fun visitAccessTypeReference(reference: Access): T
    fun visitPointerTypeReference(reference: Pointer): T
    fun visitArrayTypeReference(reference: Array): T
    fun visitFunctionTypeReference(reference: Function): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class Access(val identifier: Identifier, override val location: Location) : TypeReference() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitAccessTypeReference(this)
    }
  }

  data class Pointer(val type: TypeReference, override val location: Location) : TypeReference() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitPointerTypeReference(this)
    }
  }

  data class Array(val type: TypeReference, override val location: Location) : TypeReference() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitArrayTypeReference(this)
    }
  }

  data class Function(
    val parameters: List<TypeReference>,
    val returnType: TypeReference?,
    override val location: Location
  ) : TypeReference() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitFunctionTypeReference(this)
    }
  }
}
