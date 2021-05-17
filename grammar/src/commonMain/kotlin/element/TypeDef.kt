package com.lorenzoog.plank.grammar.element

sealed class TypeDef : PlankElement {
  interface Visitor<T> {
    fun visit(typeDef: TypeDef): T = typeDef.accept(this)

    fun visitGenericAccess(access: GenericAccess): T
    fun visitGenericUse(use: GenericUse): T
    fun visitNameTypeDef(name: Name): T
    fun visitPtrTypeDef(ptr: Ptr): T
    fun visitArrayTypeDef(array: Array): T
    fun visitFunctionTypeDef(function: Function): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class GenericAccess(
    val name: Identifier,
    override val location: Location
  ) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGenericAccess(this)
    }
  }

  data class GenericUse(
    val receiver: TypeDef,
    val arguments: List<TypeDef>,
    override val location: Location
  ) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitGenericUse(this)
    }
  }

  data class Name(val name: Identifier, override val location: Location) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitNameTypeDef(this)
    }
  }

  data class Ptr(val type: TypeDef, override val location: Location) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitPtrTypeDef(this)
    }
  }

  data class Array(val type: TypeDef, override val location: Location) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitArrayTypeDef(this)
    }
  }

  data class Function(
    val parameters: List<TypeDef>,
    val returnType: TypeDef?,
    override val location: Location
  ) : TypeDef() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitFunctionTypeDef(this)
    }
  }
}
