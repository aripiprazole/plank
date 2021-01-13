package com.lorenzoog.jplank.element

import org.antlr.v4.kotlinruntime.Token

sealed class TypeDef : PkElement {
  interface Visitor<T> {
    fun visit(typeDef: TypeDef): T = typeDef.accept(this)

    fun visitNameTypeDef(name: Name): T
    fun visitPtrTypeDef(ptr: Ptr): T
    fun visitArrayTypeDef(array: Array): T
    fun visitFunctionTypeDef(function: Function): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  data class Name(val name: Token, override val location: Location) : TypeDef() {
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
