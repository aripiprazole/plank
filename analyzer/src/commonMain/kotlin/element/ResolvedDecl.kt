package org.plank.analyzer.element

import org.plank.analyzer.infer.EnumInfo
import org.plank.analyzer.infer.EnumMemberInfo
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.FunctionInfo
import org.plank.analyzer.infer.Module
import org.plank.analyzer.infer.StructInfo
import org.plank.analyzer.infer.StructMemberInfo
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Attribute
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import org.plank.syntax.element.QualifiedPath

sealed interface ResolvedDecl : ResolvedStmt

data class ResolvedEnumDecl(val info: EnumInfo, val ty: Ty, override val location: Location) :
  ResolvedDecl {
  val name: Identifier = info.name
  val members: Map<Identifier, EnumMemberInfo> = info.members

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class ResolvedStructDecl(val info: StructInfo, val ty: Ty, override val location: Location) :
  ResolvedDecl {
  val name: Identifier = info.name
  val members: Map<Identifier, StructMemberInfo> = info.members

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class ResolvedUseDecl(
  val module: Module,
  override val location: Location,
) : ResolvedDecl {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitUseDecl(this)
  }
}

data class ResolvedModuleDecl(
  val name: QualifiedPath,
  val content: List<ResolvedDecl>,
  override val location: Location,
) : ResolvedDecl {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class ResolvedFunDecl(
  val body: ResolvedFunctionBody,
  val attributes: List<Attribute> = emptyList(),
  val references: LinkedHashMap<Identifier, Ty> = LinkedHashMap(),
  val info: FunctionInfo,
  val isNested: Boolean,
  override val ty: FunTy,
  override val location: Location,
) : ResolvedDecl, TypedPlankElement {
  val name: Identifier = info.name
  val parameters: Map<Identifier, Ty> = info.parameters
  val returnTy: Ty = info.returnTy

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
  val isNested: Boolean,
  override val ty: Ty,
  override val location: Location,
) : ResolvedDecl, TypedPlankElement {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}
