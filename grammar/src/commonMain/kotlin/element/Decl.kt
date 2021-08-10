package com.lorenzoog.plank.grammar.element

sealed class Decl : Stmt()

data class EnumDecl(
  val name: Identifier,
  val members: List<Member>,
  override val location: Location
) : Decl() {
  data class Member(val name: Identifier, val fields: List<TypeRef>)

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitEnumDecl(this)
  }
}

data class StructDecl(
  val name: Identifier,
  val properties: List<Property>,
  override val location: Location
) : Decl() {
  data class Property(val mutable: Boolean, val name: Identifier, val type: TypeRef)

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitStructDecl(this)
  }
}

data class ImportDecl(val path: QualifiedPath, override val location: Location) : Decl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitImportDecl(this)
  }
}

data class ModuleDecl(
  val path: QualifiedPath,
  val content: List<Decl>,
  override val location: Location
) : Decl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitModuleDecl(this)
  }
}

data class FunDecl(
  val modifiers: List<Modifier> = emptyList(),
  val name: Identifier,
  val type: FunctionTypeRef,
  val body: List<Stmt>,
  val realParameters: Map<Identifier, TypeRef>,
  override val location: Location
) : Decl() {
  enum class Modifier { Native }

  val isNative get() = Modifier.Native in modifiers

  val parameters = type.parameters
  val returnType = type.returnType

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitFunDecl(this)
  }
}

data class LetDecl(
  val name: Identifier,
  val mutable: Boolean,
  val type: TypeRef?,
  val value: Expr,
  override val location: Location
) : Decl() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitLetDecl(this)
  }
}

data class ErrorDecl(
  override val message: String,
  override val arguments: List<Any>
) : Decl(), ErrorPlankElement {
  override val location = Location.undefined()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitErrorDecl(this)
  }
}
