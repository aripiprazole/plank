package org.plank.resolver

import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.toIdentifier

sealed class ResolverScope {
  abstract val name: Identifier
  abstract val enclosing: ResolverScope?
  abstract val tree: ModuleTree
  abstract val content: List<Stmt>

  private val _expanded = mutableListOf<ResolverScope>()
  val expanded: List<ResolverScope> get() = _expanded

  private val _types = mutableMapOf<Identifier, ResolverTy>()
  val types: Map<Identifier, ResolverTy> get() = _types

  private val _variables = mutableMapOf<Identifier, ResolverVariable>()
  val variables: Map<Identifier, ResolverVariable> get() = _variables

  fun declare(name: Identifier) {
    _variables[name] = ResolverVariable(name, this)
  }

  fun create(name: Identifier) {
    _types[name] = ResolverTy(name, this)
  }

  fun create(name: String) {
    _types[name.toIdentifier()] = ResolverTy(name.toIdentifier(), this)
  }

  fun expand(scope: ResolverScope) {
    _expanded += scope
  }

  fun findModule(name: Identifier): Module? {
    return tree.findModule(name) ?: enclosing?.findModule(name)
  }

  fun containsVariable(name: Identifier): Boolean {
    return lookupVariable(name) != null
  }

  fun lookupTy(
    name: Identifier,
    original: ResolverScope = this,
    inScope: Boolean = true,
  ): ResolverTy? {
    if (original == this && !inScope) return null

    return _types[name]
      ?: enclosing?.lookupTy(name, original, false)
      ?: _expanded.filter { it != this }
        .firstNotNullOfOrNull { it.lookupTy(name, original, false) }
  }

  fun lookupVariable(
    name: Identifier,
    original: ResolverScope = this,
    inScope: Boolean = true,
  ): ResolverVariable? {
    if (original == this && !inScope) return null

    return _variables[name]
      ?: enclosing?.lookupVariable(name, original, false)
      ?: _expanded.filter { it != this }
        .firstNotNullOfOrNull { it.lookupVariable(name, original, false) }
  }

  abstract fun enclose(scope: ResolverScope): ResolverScope
}

object GlobalScope : ResolverScope() {
  override val name: Identifier = "Global".toIdentifier()
  override val enclosing: ResolverScope? = null
  override val tree: ModuleTree = ModuleTree()
  override val content: List<Stmt> = emptyList()

  init {
    create("Char")
    create("Bool")
    create("Double")
    create("Float")

    create("Int8")
    create("Int16")
    create("Int32")
  }

  override fun enclose(scope: ResolverScope): ResolverScope = this
}

data class FileScope(
  var file: PlankFile,
  override val enclosing: ResolverScope,
  override val tree: ModuleTree = ModuleTree(),
) : ResolverScope() {
  override val content: List<Stmt> = file.program
  override val name = file.module

  override fun enclose(scope: ResolverScope): FileScope = copy(enclosing = scope)

  override fun toString(): String = "File(${file.module}) <: $enclosing"
}

data class ModuleScope(
  override val name: Identifier,
  override val content: List<Stmt>,
  override val enclosing: ResolverScope,
  override val tree: ModuleTree = ModuleTree(),
) : ResolverScope() {
  override fun enclose(scope: ResolverScope): ModuleScope = copy(enclosing = scope)

  override fun toString(): String = "Module($name) <: $enclosing"
}

data class FunctionScope(
  override val name: Identifier,
  override val content: List<Stmt>,
  override val enclosing: ResolverScope,
  override val tree: ModuleTree = ModuleTree(),
) : ResolverScope() {
  override fun enclose(scope: ResolverScope): FunctionScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Function($name) <: $enclosing"
}

data class PatternScope(
  val pattern: Pattern,
  override val enclosing: ResolverScope,
  override val tree: ModuleTree = ModuleTree(),
) : ResolverScope() {
  override val content: List<Stmt> = emptyList()
  override val name: Identifier = "Pattern".toIdentifier()

  override fun enclose(scope: ResolverScope): PatternScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Pattern($pattern) <: $enclosing"
}

data class ClosureScope(
  override val name: Identifier,
  override val content: List<Stmt>,
  override val enclosing: ResolverScope,
  override val tree: ModuleTree = ModuleTree(),
) : ResolverScope() {
  override fun enclose(scope: ResolverScope): ClosureScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Closure($name) <: $enclosing"
}
