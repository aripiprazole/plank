package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.Attribute
import com.lorenzoog.plank.analyzer.EnumMember
import com.lorenzoog.plank.analyzer.FunctionType
import com.lorenzoog.plank.analyzer.Module
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.analyzer.StructProperty
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

sealed class TypedDecl : TypedStmt()

data class TypedEnumDecl(
  val name: Identifier,
  val members: Map<Identifier, EnumMember>,
  override val location: Location
) : TypedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class TypedStructDecl(
  val name: Identifier,
  val fields: Map<Identifier, StructProperty>,
  override val location: Location
) : TypedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class TypedImportDecl(val module: Module, override val location: Location) : TypedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitImportDecl(this)
  }
}

data class TypedModuleDecl(
  val name: Identifier,
  val content: List<TypedDecl>,
  override val location: Location
) : TypedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class TypedFunDecl(
  val name: Identifier,
  val content: List<TypedStmt>,
  val realParameters: Map<Identifier, PlankType>,
  val attributes: Map<String, Attribute> = emptyMap(),
  override val type: FunctionType,
  override val location: Location
) : TypedDecl(), TypedPlankElement {
  fun hasAttribute(name: String): Boolean {
    return attributes[name] != null
  }

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitFunDecl(this)
  }
}

data class TypedLetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : TypedDecl(), TypedPlankElement {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}
