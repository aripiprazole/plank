package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.grammar.element.Location
import org.antlr.v4.kotlinruntime.Token

sealed class TypedDecl : TypedStmt() {
  data class EnumDecl(
    val name: TypedIdentifier,
    val members: List<Member>,
    override val location: Location
  ) : TypedDecl() {
    data class Member(val name: TypedIdentifier, val fields: List<PlankType>)

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitEnumDecl(this)
    }
  }

  data class StructDecl(
    val name: TypedIdentifier,
    val fields: List<Field>,
    override val location: Location
  ) : TypedDecl() {
    data class Field(val mutable: Boolean, val name: TypedIdentifier, val type: PlankType)

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitStructDecl(this)
    }
  }

  data class ImportDecl(
    val module: TypedIdentifier,
    override val location: Location
  ) : TypedDecl() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitImportDecl(this)
    }
  }

  data class ModuleDecl(
    val name: TypedIdentifier,
    val content: List<TypedDecl>,
    override val location: Location
  ) : TypedDecl() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitModuleDecl(this)
    }
  }

  data class FunDecl(
    val modifiers: List<Modifier> = emptyList(),
    val name: TypedIdentifier,
    val body: List<TypedStmt>,
    val realParameters: Map<Token, PlankType>,
    override val type: PlankType.Callable,
    override val location: Location
  ) : TypedDecl() {
    enum class Modifier { Native }

    val isNative get() = Modifier.Native in modifiers

    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitFunDecl(this)
    }
  }

  data class LetDecl(
    val name: TypedIdentifier,
    val mutable: Boolean,
    val value: TypedExpr,
    override val type: PlankType,
    override val location: Location
  ) : TypedDecl() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitLetDecl(this)
    }
  }
}
