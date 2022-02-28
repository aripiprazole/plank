package org.plank.syntax.element

sealed interface Decl : Stmt

data class EnumDecl(
  val name: Identifier,
  val members: List<Member>,
  val generics: Set<Identifier> = emptySet(),
  override val loc: Loc = GeneratedLoc,
) : Decl {
  constructor(name: String, vararg generics: String, builder: EnumBuilder.() -> Unit) : this(
    name = name.toIdentifier(),
    members = EnumBuilder().apply(builder).toList(),
    generics = generics.toList().toIdentifier().toSet(),
  )

  data class Member(val name: Identifier, val parameters: List<TypeRef>) {
    constructor(name: Identifier, vararg parameters: TypeRef) : this(name, parameters.toList())
  }
}

data class StructDecl(
  val name: Identifier,
  val properties: List<Property>,
  val generics: Set<Identifier> = emptySet(),
  override val loc: Loc = GeneratedLoc,
) : Decl {
  constructor(name: String, vararg generics: String, builder: StructBuilder.() -> Unit) : this(
    name = name.toIdentifier(),
    properties = StructBuilder().apply(builder).toList(),
    generics = generics.toList().toIdentifier().toSet(),
  )

  data class Property(val mutable: Boolean, val name: Identifier, val type: TypeRef)
}

data class UseDecl(val path: QualifiedPath, override val loc: Loc = GeneratedLoc) :
  Decl {
  constructor(path: String, loc: Loc = GeneratedLoc) : this(path.toQualifiedPath(), loc)
}

data class ModuleDecl(
  val path: QualifiedPath,
  val content: List<Decl>,
  override val loc: Loc = GeneratedLoc,
) : Decl {
  constructor(path: String, vararg content: Decl, loc: Loc = GeneratedLoc) :
    this(path.toQualifiedPath(), content.toList(), loc)
}

data class FunDecl(
  val name: Identifier,
  val parameters: Map<Identifier, TypeRef>,
  val returnType: TypeRef,
  val body: FunctionBody,
  val attributes: List<Attribute> = emptyList(),
  override val loc: Loc = GeneratedLoc,
) : Decl {
  constructor(
    name: String,
    parameters: Map<String, TypeRef>,
    returnType: TypeRef,
    body: FunctionBody,
    loc: Loc = GeneratedLoc,
  ) : this(
    name = name.toIdentifier(),
    parameters = parameters.mapKeys { it.key.toIdentifier() },
    returnType = returnType,
    body = body,
    loc = loc,
  )
}

data class LetDecl(
  val name: Identifier,
  val value: Expr,
  val type: TypeRef? = null,
  val mutable: Boolean = false,
  override val loc: Loc = GeneratedLoc,
) : Decl {
  constructor(name: String, value: Expr, type: TypeRef? = null, loc: Loc = GeneratedLoc) :
    this(name.toIdentifier(), value, type, loc = loc)
}

class EnumBuilder {
  private val list = mutableListOf<EnumDecl.Member>()

  fun member(name: String, vararg parameters: TypeRef) {
    list += EnumDecl.Member(name.toIdentifier(), parameters.toList())
  }

  internal fun toList(): List<EnumDecl.Member> = list
}

class StructBuilder {
  private val list = mutableListOf<StructDecl.Property>()

  fun property(mutable: Boolean, name: String, type: TypeRef) {
    list += StructDecl.Property(mutable, name.toIdentifier(), type)
  }

  internal fun toList(): List<StructDecl.Property> = list
}
