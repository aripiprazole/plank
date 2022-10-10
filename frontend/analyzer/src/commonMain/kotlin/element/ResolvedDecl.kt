package org.plank.analyzer.element

import org.plank.analyzer.checker.EnumInfo
import org.plank.analyzer.checker.EnumMemberInfo
import org.plank.analyzer.checker.FunctionInfo
import org.plank.analyzer.checker.Scope
import org.plank.analyzer.checker.StructInfo
import org.plank.analyzer.checker.StructMemberInfo
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Attribute
import org.plank.syntax.element.GeneratedLoc
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc
import org.plank.syntax.element.QualifiedPath

sealed interface ResolvedDecl : ResolvedStmt

data class ResolvedEnumDecl(val info: EnumInfo, override val loc: Loc) :
  ResolvedDecl {
  val ty: Ty = info.ty
  val name: Identifier = info.name
  val members: Map<Identifier, EnumMemberInfo> = info.members
}

data class ResolvedStructDecl(val info: StructInfo, override val loc: Loc) :
  ResolvedDecl {
  val ty: Ty = info.ty
  val name: Identifier = info.name
  val members: Map<Identifier, StructMemberInfo> = info.members
}

data class ResolvedUseDecl(
  val module: Scope,
  override val loc: Loc,
) : ResolvedDecl

data class ResolvedModuleDecl(
  val name: QualifiedPath,
  val content: List<ResolvedDecl>,
  override val loc: Loc,
) : ResolvedDecl

data class ResolvedFunDecl(
  val body: ResolvedFunctionBody,
  val attributes: List<Attribute> = emptyList(),
  val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  val info: FunctionInfo,
  val isNested: Boolean,
  override val loc: Loc,
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
}

data class ResolvedLetDecl(
  val name: Identifier,
  val value: TypedExpr,
  val scheme: Scheme,
  val ty: Ty,
  val isNested: Boolean = false,
  val mutable: Boolean = false,
  val subst: Subst = Subst(),
  override val loc: Loc = GeneratedLoc,
) : ResolvedDecl
