package com.gabrielleeg1.plank.analyzer.element

import com.gabrielleeg1.plank.analyzer.EnumMember
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.Module
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.StructProperty
import com.gabrielleeg1.plank.grammar.debug.DontDump
import com.gabrielleeg1.plank.grammar.element.Attribute
import com.gabrielleeg1.plank.grammar.element.ErrorPlankElement
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.QualifiedPath

sealed interface ResolvedDecl : ResolvedStmt

data class ResolvedEnumDecl(
  val name: Identifier,
  val members: Map<Identifier, EnumMember>,
  override val type: PlankType,
  override val location: Location
) : ResolvedDecl, TypedPlankElement {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class ResolvedStructDecl(
  val name: Identifier,
  val properties: Map<Identifier, StructProperty>,
  override val type: PlankType,
  override val location: Location,
) : ResolvedDecl, TypedPlankElement {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class ResolvedImportDecl(
  val module: Module,
  override val location: Location
) : ResolvedDecl {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitImportDecl(this)
  }
}

data class ResolvedModuleDecl(
  val name: QualifiedPath,
  val content: List<ResolvedDecl>,
  override val location: Location
) : ResolvedDecl {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class ResolvedFunDecl(
  val name: Identifier,
  val content: List<ResolvedStmt>,
  val realParameters: Map<Identifier, PlankType>,
  val attributes: List<Attribute> = emptyList(),
  val references: LinkedHashMap<Identifier, PlankType> = LinkedHashMap(),
  override val type: FunctionType,
  override val location: Location
) : ResolvedDecl, TypedPlankElement {
  @DontDump
  val parameters = type.parameters

  val returnType = run {
    var current: PlankType = type.returnType

    while (current is FunctionType) {
      current = current.returnType
    }

    current
  }

  fun attribute(name: String): Attribute? {
    return attributes.find { it.name.text == name }
  }

  fun hasAttribute(name: String): Boolean {
    return attributes.any { it.name.text == name }
  }

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitFunDecl(this)
  }
}

data class ResolvedLetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val value: TypedExpr,
  override val type: PlankType,
  override val location: Location,
) : ResolvedDecl, TypedPlankElement {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}

data class ResolvedErrorDecl(
  override val message: String,
  override val arguments: List<Any>,
  override val location: Location = Location.Generated,
) : ResolvedDecl, ErrorPlankElement {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitViolatedDecl(this)
  }
}
