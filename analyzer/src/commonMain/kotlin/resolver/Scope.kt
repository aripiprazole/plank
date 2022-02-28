package org.plank.analyzer.resolver

import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.text
import org.plank.syntax.element.toIdentifier

sealed class Scope {
  abstract val name: Identifier
  abstract val enclosing: Scope?
  abstract val tree: ModuleTree
  abstract val content: List<Stmt>

  open val isTopLevelScope: Boolean = true

  open val nested: Boolean get() = enclosing != null
  open val module: Module get() = enclosing!!.module
  open val letters: Sequence<String> get() = enclosing!!.letters
  open val names: Set<String> get() = enclosing!!.names

  open val references = mutableMapOf<Identifier, Ty>()

  private val _expanded = mutableListOf<Scope>()
  val expanded: List<Scope> get() = _expanded

  private val _types = mutableMapOf<Identifier, TyInfo>()
  val types: Map<Identifier, TyInfo> get() = _types

  private val _variables = mutableMapOf<Identifier, Variable>()
  val variables: Map<Identifier, Variable> get() = _variables

  /**
   * Declares a compiler-defined variable with type [scheme] in the context
   */
  fun declare(name: Identifier, scheme: Scheme, mutable: Boolean = false): Scheme {
    _variables[name] = RankedVariable(scheme, mutable, name, this)
    return scheme
  }

  fun declare(name: Identifier, ty: Ty, mutable: Boolean = false): Scheme {
    _variables[name] = LocalVariable(mutable, name, ty, this)
    return Scheme(emptySet(), ty)
  }

  fun declare(name: Identifier, value: TypedExpr, mutable: Boolean = false): Scheme {
    val scheme = Scheme(emptySet(), value.ty)
    _variables[name] = LocalVariable(mutable, name, scheme.ty, this)
    return scheme
  }

  fun declareInline(
    name: String,
    returnTy: Ty,
    vararg parameters: Ty,
    builder: (List<TypedExpr>) -> TypedExpr,
  ) {
    _variables[Identifier(name)] =
      InlineVariable(
        false,
        Identifier(name),
        FunTy(returnTy, parameters.toList()),
        this,
        false
      ) {
        ResolvedExprBody(builder(it))
      }
  }

  fun <T : TyInfo> create(info: T): T {
    _types[info.name] = info
    return info
  }

  fun expand(scope: Scope) {
    _expanded += scope
  }

  fun findModule(name: Identifier): Module? {
    return tree.findModule(name)
      ?: enclosing?.findModule(name)
  }

  fun findTyInfo(name: Identifier, original: Scope = this, inScope: Boolean = true): TyInfo? {
    if (original == this && !inScope) return null

    return _types[name]
      ?: enclosing?.findTyInfo(name, original, false)
      ?: _expanded
        .filter { it != this }
        .firstNotNullOfOrNull { it.findTyInfo(name, original, false) }
  }

  fun containsVariable(name: Identifier): Boolean {
    return lookupVariable(name) != null
  }

  fun lookupVariable(name: Identifier, original: Scope = this, inScope: Boolean = true): Variable? {
    if (original == this && !inScope) return null

    return _variables[name]?.inScope()
      ?: enclosing?.lookupVariable(name, original, false)?.notInScope()
      ?: _expanded.filter { it != this }
        .firstNotNullOfOrNull { it.lookupVariable(name, original, false) }
        ?.notInScope()
  }

  abstract fun enclose(scope: Scope): Scope
}

data class FileScope(
  var file: PlankFile,
  override val module: Module,
  override val enclosing: GlobalScope,
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val content: List<Stmt> = file.program
  override val name = file.module
  override val nested = false

  override fun enclose(scope: Scope): FileScope = when (scope) {
    is GlobalScope -> copy(enclosing = scope)
    else -> this
  }

  override fun toString(): String =
    "File(${file.module}) <: $enclosing"
}

data class ModuleScope(
  override val module: Module,
  override val enclosing: Scope,
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val content: List<Stmt> = module.content
  override val name: Identifier = module.name

  override fun enclose(scope: Scope): ModuleScope = copy(enclosing = scope)

  override fun toString(): String =
    "Module(${module.name}) <: $enclosing"
}

data class FunctionScope(
  val function: FunctionInfo,
  override val content: List<Stmt>,
  override val enclosing: Scope? = null,
  override val tree: ModuleTree = ModuleTree(),
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val name: Identifier = function.name,
) : Scope() {
  override val names: Set<String> = function.generics.text().toSet()
  override val isTopLevelScope: Boolean = false

  val returnTy: Ty = function.returnTy
  val parameters: Map<Identifier, Ty> = function.parameters

  override fun enclose(scope: Scope): FunctionScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String {
    val names = when {
      names.isEmpty() -> ""
      else -> "[${names.joinToString(" ") { "'$it" }}]"
    }
    return "Function$names($name, ${function.ty}) <: $enclosing"
  }
}

data class PatternScope(
  val pattern: Pattern,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val content: List<Stmt> = emptyList()
  override val name: Identifier = "Pattern".toIdentifier()
  override val isTopLevelScope: Boolean = false

  override fun enclose(scope: Scope): PatternScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Pattern($pattern) <: $enclosing"
}

data class ClosureScope(
  override val name: Identifier,
  override val content: List<Stmt>,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val isTopLevelScope: Boolean = false

  override fun enclose(scope: Scope): ClosureScope = copy(enclosing = scope)

  override fun hashCode(): Int = super.hashCode()
  override fun equals(other: Any?): Boolean = super.equals(other)
  override fun toString(): String = "Closure($name) <: $enclosing"
}
