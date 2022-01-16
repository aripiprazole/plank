package com.gabrielleeg1.plank.grammar.element

sealed interface Decl : Stmt

data class EnumDecl(
  val name: Identifier,
  val members: List<Member>,
  override val location: Location
) : Decl {
  data class Member(val name: Identifier, val parameters: List<TypeRef>)
}

data class StructDecl(
  val name: Identifier,
  val properties: List<Property>,
  override val location: Location
) : Decl {
  data class Property(val mutable: Boolean, val name: Identifier, val type: TypeRef)
}

data class ImportDecl(val path: QualifiedPath, override val location: Location) : Decl

data class ModuleDecl(
  val path: QualifiedPath,
  val content: List<Decl>,
  override val location: Location
) : Decl

data class FunDecl(
  val attributes: List<Attribute> = emptyList(),
  val name: Identifier,
  val type: FunctionTypeRef,
  val body: List<Stmt>,
  val realParameters: Map<Identifier, TypeRef>,
  override val location: Location
) : Decl

data class LetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val type: TypeRef?,
  val value: Expr,
  override val location: Location
) : Decl

data class ErrorDecl(
  override val message: String,
  override val arguments: List<Any>
) : Decl, ErrorPlankElement {
  override val location = Location.Generated
}
