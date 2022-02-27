package org.plank.analyzer.element

import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.resolver.EnumInfo
import org.plank.analyzer.resolver.EnumMemberInfo
import org.plank.analyzer.resolver.FunctionInfo
import org.plank.analyzer.resolver.Module
import org.plank.analyzer.resolver.StructInfo
import org.plank.analyzer.resolver.StructMemberInfo
import org.plank.syntax.element.Attribute
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import org.plank.syntax.element.QualifiedPath

sealed interface ResolvedDecl : ResolvedStmt

data class ResolvedEnumDecl(val info: EnumInfo, override val location: Location) :
  ResolvedDecl {
  val ty: Ty = info.ty
  val name: Identifier = info.name
  val members: Map<Identifier, EnumMemberInfo> = info.members

  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class ResolvedStructDecl(val info: StructInfo, override val location: Location) :
  ResolvedDecl {
  val ty: Ty = info.ty
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
  val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  val info: FunctionInfo,
  val isNested: Boolean,
  override val location: Location,
) : ResolvedDecl {
  val ty: FunTy = info.ty
  val scheme: Scheme = info.scheme
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
  val value: TypedExpr,
  val scheme: Scheme,
  val ty: Ty,
  val isNested: Boolean = false,
  val mutable: Boolean = false,
  val subst: Subst = Subst(),
  override val location: Location = Location.Generated,
) : ResolvedDecl {
  override fun <T> accept(visitor: ResolvedStmt.Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}
