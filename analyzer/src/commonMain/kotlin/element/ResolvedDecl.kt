package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.Attribute
import com.lorenzoog.plank.analyzer.EnumMember
import com.lorenzoog.plank.analyzer.FunctionType
import com.lorenzoog.plank.analyzer.Module
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.analyzer.StructProperty
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location

sealed class ResolvedDecl : ResolvedStmt()

data class ResolvedEnumDecl(
  val name: Identifier,
  val members: Map<Identifier, EnumMember>,
  override val location: Location
) : ResolvedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class ResolvedStructDecl(
  val name: Identifier,
  val fields: Map<Identifier, StructProperty>,
  override val location: Location
) : ResolvedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class ResolvedImportDecl(
  val module: Module,
  override val location: Location
) : ResolvedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitImportDecl(this)
  }
}

data class ResolvedModuleDecl(
  val name: Identifier,
  val content: List<ResolvedDecl>,
  override val location: Location
) : ResolvedDecl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class ResolvedFunDecl(
  val name: Identifier,
  val content: List<ResolvedStmt>,
  val realParameters: Map<Identifier, PlankType>,
  val attributes: Map<String, Attribute> = emptyMap(),
  override val type: FunctionType,
  override val location: Location
) : ResolvedDecl(), TypedPlankElement {
  fun hasAttribute(name: String): Boolean {
    return attributes[name] != null
  }

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitFunDecl(this)
  }
}

data class ResolvedLetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location
) : ResolvedDecl(), TypedPlankElement {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}

data class ViolatedDecl(
  override val message: String,
  override val arguments: List<Any>,
) : ResolvedDecl(), ViolatedPlankElement {
  override val location = Location.undefined()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitViolatedDecl(this)
  }
}
