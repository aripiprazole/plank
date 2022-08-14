package org.plank.analyzer.infer

import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.text
import org.plank.syntax.element.toIdentifier

sealed class Scope {
  abstract val name: Identifier
  abstract val enclosing: Scope?

  open val isTopLevelScope: Boolean = true
  open val references: MutableMap<Identifier, Ty> = mutableMapOf()

  private val _expanded: MutableList<Scope> = mutableListOf()
  private val _modules: MutableMap<Identifier, Scope> = mutableMapOf()
  private val _types: MutableMap<Identifier, TyInfo> = mutableMapOf()
  private val _variables: MutableMap<Identifier, Variable> = mutableMapOf()

  val types: Map<Identifier, TyInfo> get() = _types
  val expanded: List<Scope> get() = _expanded
  val variables: Map<Identifier, Variable> get() = _variables

  fun expand(scope: Scope) {
    _expanded += scope
  }

  fun createModule(scope: Scope): Scope {
    _modules[scope.name] = scope
    return scope
  }

  fun lookupModule(name: Identifier): Scope? {
    return _modules[name] ?: enclosing?.lookupModule(name)
  }

  fun <T : TyInfo> createTyInfo(info: T): T {
    _types[info.name] = info
    return info
  }

  fun lookupTyInfo(name: Identifier, original: Scope = this, inScope: Boolean = true): TyInfo? {
    if (original == this && !inScope) return null

    return _types[name]
      ?: enclosing?.lookupTyInfo(name, original, false)
      ?: _expanded
        .filter { it != this }
        .firstNotNullOfOrNull { it.lookupTyInfo(name, original, false) }
  }

  /**
   * Declares a compiler-defined variable with type [scheme] in the context
   */
  fun declare(name: Identifier, scheme: Scheme, mutable: Boolean = false): Scheme {
    _variables[name] = RankedVariable(scheme, mutable, name, this)
    return scheme
  }

  fun declare(name: Identifier, ty: Ty, mutable: Boolean = false): Scheme {
    _variables[name] = LocalVariable(mutable, name, ty, Scheme(ty), this)
    return Scheme(emptySet(), ty)
  }

  fun declare(variable: Variable): Scheme {
    _variables[variable.name] = variable
    return variable.scheme
  }

  fun declareInline(name: String, returnTy: Ty, vararg parameters: Ty, builder: InlineBuilder) {
    val ty = FunTy(returnTy, parameters.toList())
    val id = name.toIdentifier()

    _variables[id] = InlineVariable(false, id, ty, Scheme(ty), this, false) {
      builder(it)
    }
  }

  fun lookupVariable(name: Identifier, original: Scope = this, inScope: Boolean = true): Variable? {
    if (original == this && !inScope) return null

    return _variables[name]?.inScope()
      ?: enclosing?.lookupVariable(name, original, false)?.notInScope()
      ?: _expanded.filter { it != this }
        .firstNotNullOfOrNull { it.lookupVariable(name, original, false) }
        ?.notInScope()
  }

  fun containsVariable(name: Identifier): Boolean {
    return lookupVariable(name) != null
  }

  abstract fun enclose(scope: Scope): Scope
}

data class ModuleScope(
  override val name: Identifier,
  override val enclosing: Scope,
) : Scope() {
  override val isTopLevelScope: Boolean = enclosing.isTopLevelScope

  override fun enclose(scope: Scope): ModuleScope = copy(enclosing = scope)

  override fun toString(): String = "Module($name) <: $enclosing"
}

data class PatternScope(
  val pattern: Pattern,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
) : Scope() {
  override val name: Identifier = "Pattern".toIdentifier()
  override val isTopLevelScope: Boolean = false

  override fun enclose(scope: Scope): PatternScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Pattern($pattern) <: $enclosing"
}

data class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
) : Scope() {
  override val isTopLevelScope: Boolean = false

  override fun enclose(scope: Scope): ClosureScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Closure($name) <: $enclosing"
}

data class FileScope(
  var file: PlankFile,
  override val enclosing: Scope,
  override val name: Identifier = file.module,
) : Scope() {

  override fun enclose(scope: Scope): FileScope = copy(enclosing = scope)

  override fun toString(): String = "File(${file.module}) <: $enclosing"
}

data class FunctionScope(
  val function: FunctionInfo,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val name: Identifier = function.name,
) : Scope() {
  override val isTopLevelScope: Boolean = false

  val returnTy: Ty = function.returnTy
  val parameters: Map<Identifier, Ty> = function.parameters

  override fun enclose(scope: Scope): FunctionScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String {
    val generics = function.generics.text().toSet()
    val names = when {
      generics.isEmpty() -> ""
      else -> "[${generics.joinToString(" ") { "'$it" }}]"
    }

    return "Function$names($name, ${function.ty}) <: $enclosing"
  }
}

fun Scope.fullPath(): QualifiedPath = when (this) {
  is ModuleScope -> QualifiedPath(name.text, name.loc)
  is FileScope -> QualifiedPath(name.text, name.loc)
  else -> QualifiedPath()
}
