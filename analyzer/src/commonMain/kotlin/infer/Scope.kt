package org.plank.analyzer.infer

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

data class Variable(
  val mutable: Boolean,
  val name: Identifier,
  val ty: Ty,
  val declaredIn: Scope,
  val isInScope: Boolean = false,
) {
  override fun toString(): String {
    return "Variable(mutable=$mutable, name=$name, ty=$ty, isInScope=$isInScope)"
  }
}

class GlobalScope(override val moduleTree: ModuleTree) : Scope() {
  /**
   * Init compiler-defined functions
   */
  init {
    create(IntInfo("Char", 8))
    create(IntInfo("Bool", 8))
    create(DoubleInfo)
    create(FloatInfo)

    create(IntInfo("Int8", 8))
    create(IntInfo("Int16", 16))
    create(IntInfo("Int32", 32))

    // Add default binary operators
    inlineFun("+", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntAddExpr(a, b) }
    inlineFun("-", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntSubExpr(a, b) }
    inlineFun("*", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntMulExpr(a, b) }
    inlineFun("/", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntDivExpr(a, b) }

    // Add default logical operators
    inlineFun("==", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntEQExpr(a, b) }
    inlineFun("!=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntNEQExpr(a, b) }
    inlineFun(">=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTEExpr(a, b) }
    inlineFun(">", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTExpr(a, b) }
    inlineFun("<=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTEExpr(a, b) }
    inlineFun("<", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTExpr(a, b) }
  }

  override val name = Identifier("Global")
  override val enclosing: Scope? = null

  private fun inlineFun(
    name: String,
    returnTy: Ty,
    vararg parameters: Ty,
    builder: (List<TypedExpr>) -> TypedExpr,
  ) {
    declare(Identifier(name), arrowTy(returnTy, parameters.toList()))
  }
}

data class FileScope(
  val file: PlankFile,
  override val enclosing: Scope? = null,
  override val moduleTree: ModuleTree = ModuleTree(),
) : Scope() {
  override val name = file.module
  override val nested = false
}

data class ModuleScope(
  val module: Module,
  override val enclosing: Scope,
  override val moduleTree: ModuleTree = ModuleTree()
) : Scope() {
  override val name: Identifier = Identifier("${enclosing.name}.${module.name}")
}

class FunctionScope(
  val function: FunctionInfo,
  override val name: Identifier,
  override val enclosing: Scope? = null,
  override val moduleTree: ModuleTree = ModuleTree(),
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
) : Scope() {
  override val isTopLevelScope: Boolean = false

  val returnTy: Ty = function.returnTy
  val parameters: Map<Identifier, Ty> = function.parameters
}

open class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
  override val references: MutableMap<Identifier, Ty> = LinkedHashMap(),
  override val moduleTree: ModuleTree = ModuleTree()
) : Scope() {
  override val isTopLevelScope: Boolean = false
}

sealed class Scope {
  abstract val name: Identifier
  abstract val enclosing: Scope?
  abstract val moduleTree: ModuleTree

  open val isTopLevelScope: Boolean = true
  open val nested: Boolean get() = enclosing != null

  open val references = mutableMapOf<Identifier, Ty>()

  private val types = mutableMapOf<Identifier, TyInfo>()
  private val expanded = mutableListOf<Scope>()
  private val variables = mutableMapOf<Identifier, Variable>()

  fun expand(another: Scope): Scope {
    expanded += another

    return this
  }

  /**
   * Declares a compiler-defined variable with type [ty] in the context
   */
  fun declare(name: Identifier, ty: Ty, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, ty, this)
  }

  fun declare(name: Identifier, value: TypedExpr, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, value.ty, this)
  }

  fun <T : TyInfo> create(info: T): T {
    return create(info.name, info)
  }

  fun <T : TyInfo> create(name: Identifier, info: T): T {
    types[name] = info
    return info
  }

  fun findModule(name: Identifier): Module? {
    return moduleTree.findModule(name)
      ?: enclosing?.findModule(name)
  }

  fun findTyInfo(name: Identifier): TyInfo? {
    return types[name]
      ?: enclosing?.findTyInfo(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findTyInfo(name) }
  }

  fun findVariable(name: Identifier): Variable? {
    return variables[name]?.copy(isInScope = true)
      ?: enclosing?.findVariable(name)?.copy(isInScope = false)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariable(name) }
        ?.copy(isInScope = false)
  }
}
