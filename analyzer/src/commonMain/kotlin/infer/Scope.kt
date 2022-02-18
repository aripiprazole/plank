package org.plank.analyzer.infer

import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedFunctionBody
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

sealed interface Variable {
  val mutable: Boolean
  val name: Identifier
  val ty: Ty
  val declaredIn: Scope
  val isInScope: Boolean

  fun name(name: Identifier): Variable

  fun inScope(): Variable
  fun notInScope(): Variable
}

data class SimpleVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val ty: Ty,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
) : Variable {
  override fun name(name: Identifier): SimpleVariable = copy(name = name)
  override fun inScope(): SimpleVariable = copy(isInScope = true)
  override fun notInScope(): SimpleVariable = copy(isInScope = false)

  override fun toString(): String =
    "SimpleVariable(mutable=$mutable, name=$name, ty=$ty, isInScope=$isInScope)"
}

data class InlineVariable(
  override val mutable: Boolean,
  override val name: Identifier,
  override val ty: Ty,
  override val declaredIn: Scope,
  override val isInScope: Boolean = false,
  val inlineCall: (List<TypedExpr>) -> ResolvedFunctionBody,
) : Variable {
  override fun name(name: Identifier): InlineVariable = copy(name = name)
  override fun inScope(): InlineVariable = copy(isInScope = true)
  override fun notInScope(): InlineVariable = copy(isInScope = false)

  override fun toString(): String =
    "InlineVariable(mutable=$mutable, name=$name, ty=$ty, isInScope=$isInScope)"
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

  override val name = Identifier("Global")
  override val enclosing: Scope? = null
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
  override val moduleTree: ModuleTree = ModuleTree(),
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
  override val moduleTree: ModuleTree = ModuleTree(),
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

  /**
   * Declares a compiler-defined variable with type [ty] in the context
   */
  fun declare(name: Identifier, ty: Ty, mutable: Boolean = false) {
    variables[name] = SimpleVariable(mutable, name, ty, this)
  }

  fun declare(name: Identifier, value: TypedExpr, mutable: Boolean = false) {
    variables[name] = SimpleVariable(mutable, name, value.ty, this)
  }

  fun declareInline(
    name: String,
    returnTy: Ty,
    vararg parameters: Ty,
    builder: (List<TypedExpr>) -> TypedExpr,
  ) {
    variables[Identifier(name)] =
      InlineVariable(false, Identifier(name), FunTy(returnTy, parameters.toList()), this, false) {
        ResolvedExprBody(builder(it))
      }
  }

  fun <T : TyInfo> create(info: T): T {
    types[info.name] = info
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
    return variables[name]?.inScope()
      ?: enclosing?.findVariable(name)?.notInScope()
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariable(name) }
        ?.notInScope()
  }
}
