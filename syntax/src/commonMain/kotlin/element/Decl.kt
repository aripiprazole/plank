package org.plank.syntax.element

sealed interface Decl : Stmt

data class EnumDecl(
  val name: Identifier,
  val members: List<Member>,
  val generics: Set<Identifier> = emptySet(),
  override val location: Location = Location.Generated,
) : Decl {
  data class Member(val name: Identifier, val parameters: List<TypeRef>) {
    constructor(name: Identifier, vararg parameters: TypeRef) : this(name, parameters.toList())
  }
}

data class StructDecl(
  val name: Identifier,
  val generics: Set<Identifier>,
  val properties: List<Property>,
  override val location: Location = Location.Generated,
) : Decl {
  data class Property(val mutable: Boolean, val name: Identifier, val type: TypeRef)
}

data class UseDecl(val path: QualifiedPath, override val location: Location = Location.Generated) :
  Decl

data class ModuleDecl(
  val path: QualifiedPath,
  val content: List<Decl>,
  override val location: Location = Location.Generated,
) : Decl {
  constructor(path: QualifiedPath, vararg content: Decl, location: Location = Location.Generated) :
    this(path, content.toList(), location)
}

data class FunDecl(
  val name: Identifier,
  val parameters: Map<Identifier, TypeRef>,
  val returnType: TypeRef,
  val body: FunctionBody,
  val attributes: List<Attribute> = emptyList(),
  override val location: Location = Location.Generated,
) : Decl

data class LetDecl(
  val name: Identifier,
  val value: Expr,
  val type: TypeRef? = null,
  val mutable: Boolean = false,
  override val location: Location = Location.Generated,
) : Decl
