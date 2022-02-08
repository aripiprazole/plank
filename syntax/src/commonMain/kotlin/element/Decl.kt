package org.plank.syntax.element

sealed interface Decl : Stmt

data class EnumDecl(
  val name: Identifier,
  val members: List<Member>,
  override val location: Location
) : Decl {
  data class Member(val name: Identifier, val parameters: List<TypeRef>)

  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class StructDecl(
  val name: Identifier,
  val properties: List<Property>,
  override val location: Location
) : Decl {
  data class Property(val mutable: Boolean, val name: Identifier, val type: TypeRef)

  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class ImportDecl(val path: QualifiedPath, override val location: Location) : Decl {
  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitImportDecl(this)
  }
}

data class ModuleDecl(
  val path: QualifiedPath,
  val content: List<Decl>,
  override val location: Location
) : Decl {
  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class FunDecl(
  val attributes: List<Attribute> = emptyList(),
  val name: Identifier,
  val type: FunctionTypeRef,
  val body: FunctionBody,
  override val location: Location
) : Decl {
  val realParameters: Map<Identifier, TypeRef> = type.realParameters

  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitFunDecl(this)
  }
}

data class LetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val type: TypeRef?,
  val value: Expr,
  override val location: Location
) : Decl {
  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}

data class ErrorDecl(
  override val message: String,
  override val arguments: List<Any>
) : Decl, ErrorPlankElement {
  override val location = Location.Generated

  override fun <T> accept(visitor: Stmt.Visitor<T>): T {
    return visitor.visitErrorDecl(this)
  }
}
