package org.plank.analyzer.infer

import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedIntAddExpr
import org.plank.analyzer.element.TypedIntDivExpr
import org.plank.analyzer.element.TypedIntEQExpr
import org.plank.analyzer.element.TypedIntGTEExpr
import org.plank.analyzer.element.TypedIntGTExpr
import org.plank.analyzer.element.TypedIntLTEExpr
import org.plank.analyzer.element.TypedIntLTExpr
import org.plank.analyzer.element.TypedIntMulExpr
import org.plank.analyzer.element.TypedIntNEQExpr
import org.plank.analyzer.element.TypedIntSubExpr
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.text

class GlobalScope(override val tree: ModuleTree) : Scope() {
  /**
   * Init compiler-defined functions
   */
  init {
    create(IntInfo("Char", charTy, 8))
    create(IntInfo("Bool", boolTy, 8))
    create(DoubleInfo)
    create(FloatInfo)

    create(IntInfo("Int8", i8Ty, 8))
    create(IntInfo("Int16", i16Ty, 16))
    create(IntInfo("Int32", i32Ty, 32))

    // Add default binary operators
    declareInline("+", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntAddExpr(a, b) }
    declareInline("-", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntSubExpr(a, b) }
    declareInline("*", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntMulExpr(a, b) }
    declareInline("/", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntDivExpr(a, b) }

    // Add default logical operators
    declareInline("==", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntEQExpr(a, b) }
    declareInline("!=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntNEQExpr(a, b) }
    declareInline(">=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTEExpr(a, b) }
    declareInline(">", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTExpr(a, b) }
    declareInline("<=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTEExpr(a, b) }
    declareInline("<", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTExpr(a, b) }
  }

  private var count: Int = 0

  override val name = Identifier("Global")
  override val enclosing: Scope? = null
  override val names: Set<String> = emptySet()

  override fun fresh(): Ty {
    return VarTy(letters.elementAt(count)).also { count++ }
  }

  override val letters: Sequence<String> = sequence {
    var prefix = ""
    var i = 0
    while (true) {
      i++
      ('a'..'z').forEach { c ->
        yield("$prefix$c")
      }
      if (i > Char.MAX_VALUE.code) {
        i = 0
      }
      prefix += "${i.toChar()}"
    }
  }

  override fun toString(): String = "Global"
}

data class FileScope(
  val file: PlankFile,
  override val module: Module,
  override val enclosing: Scope? = null,
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val name = file.module
  override val nested = false

  override fun toString(): String =
    "File(${file.path}, tree=$tree) <: $enclosing"
}

data class ModuleScope(
  override val module: Module,
  override val enclosing: Scope,
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val name: Identifier = Identifier("${enclosing.name}.${module.name}")
}

class FunctionScope(
  val function: FunctionInfo,
  override val name: Identifier,
  override val enclosing: Scope? = null,
  override val tree: ModuleTree = ModuleTree(),
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
) : Scope() {
  override val names: Set<String> = function.generics.text().toSet()
  override val isTopLevelScope: Boolean = false

  val returnTy: Ty = function.returnTy
  val parameters: Map<Identifier, Ty> = function.parameters

  private fun showNames(): String {
    return when {
      names.isEmpty() -> ""
      else -> "[${names.joinToString(" ")}]"
    }
  }

  override fun toString(): String = "Function${showNames()}($name, ${function.ty}) <: $enclosing"
}

open class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val tree: ModuleTree = ModuleTree(),
) : Scope() {
  override val isTopLevelScope: Boolean = false

  override fun toString(): String = "Closure($name) <: $enclosing"
}

sealed class Scope {
  abstract val name: Identifier
  abstract val enclosing: Scope?
  abstract val tree: ModuleTree

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

  fun findModule(name: Identifier): Module? {
    return tree.findModule(name)
      ?: enclosing?.findModule(name)
  }

  fun findTyInfo(name: Identifier): TyInfo? {
    return _types[name]
      ?: enclosing?.findTyInfo(name)
      ?: _expanded.filter { it != this }.firstNotNullOfOrNull { it.findTyInfo(name) }
  }

  fun findVariable(name: Identifier): Variable? {
    return _variables[name]?.inScope()
      ?: enclosing?.findVariable(name)?.notInScope()
      ?: _expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariable(name) }
        ?.notInScope()
  }

  open fun fresh(): Ty = enclosing!!.fresh()
}
