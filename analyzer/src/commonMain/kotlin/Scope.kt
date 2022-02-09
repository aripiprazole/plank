package org.plank.analyzer

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
import org.plank.syntax.element.Location
import org.plank.syntax.element.PlankFile

data class Variable(
  val mutable: Boolean,
  val name: Identifier,
  var value: TypedExpr,
  val declaredIn: Scope,
  val isInScope: Boolean = false,
) {
  val type get() = value.type

  override fun toString(): String {
    return "Variable(mutable=$mutable, name=$name, value=$value, isInScope=$isInScope)"
  }
}

class GlobalScope(override val moduleTree: ModuleTree) : Scope() {
  private val i32 = IntType(32)
  private val f32 = FloatType(32)
  private val bool = BoolType

  /**
   * Init compiler-defined functions
   */
  init {
    create(CharType)
    create(bool)
    create(i32)
    create(f32)

    // Add default binary operators
    inlineFun("+", i32, i32, i32) { (a, b) -> TypedIntAddExpr(a, b) }
    inlineFun("-", i32, i32, i32) { (a, b) -> TypedIntSubExpr(a, b) }
    inlineFun("*", i32, i32, i32) { (a, b) -> TypedIntMulExpr(a, b) }
    inlineFun("/", i32, i32, i32) { (a, b) -> TypedIntDivExpr(a, b) }

    // Add default logical operators
    inlineFun("==", bool, i32, i32) { (a, b) -> TypedIntEQExpr(a, b) }
    inlineFun("!=", bool, i32, i32) { (a, b) -> TypedIntNEQExpr(a, b) }
    inlineFun(">=", bool, i32, i32) { (a, b) -> TypedIntGTEExpr(a, b) }
    inlineFun(">", bool, i32, i32) { (a, b) -> TypedIntGTExpr(a, b) }
    inlineFun("<=", bool, i32, i32) { (a, b) -> TypedIntLTEExpr(a, b) }
    inlineFun("<", bool, i32, i32) { (a, b) -> TypedIntLTExpr(a, b) }
  }

  override val name = Identifier("Global")
  override val enclosing: Scope? = null

  private fun inlineFun(
    name: String,
    returnType: PlankType,
    vararg parameters: PlankType,
    builder: (List<TypedExpr>) -> TypedExpr,
  ) {
    declare(
      Identifier(name),
      FunctionType(
        returnType,
        parameters.toList(),
        parameters.withIndex().associate { Identifier(it.index.toString()) to it.value }
      ).copy(isInline = true, actualReturnType = returnType, inlineCall = {
        ResolvedExprBody(builder(it))
      })
    )
  }
}

data class AttributeScope(override val moduleTree: ModuleTree) : Scope() {
  override val name: Identifier = Identifier("ATTRIBUTE_SCOPE")
  override val enclosing: Scope? = null

  init {
    create(UnitType)
    create(CharType)
    create(BoolType)
    create(Identifier("String"), PointerType(CharType))
    create(Identifier("Int"), IntType(32))
    create(Identifier("Float"), FloatType(32))
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
  val function: FunctionType,
  override val name: Identifier,
  override val enclosing: Scope? = null,
  override val moduleTree: ModuleTree = ModuleTree(),
  override val references: LinkedHashMap<Identifier, PlankType> = LinkedHashMap(),
) : Scope() {
  val returnType get() = function.actualReturnType

  override val isTopLevelScope: Boolean = false
}

open class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
  override val references: LinkedHashMap<Identifier, PlankType> = LinkedHashMap(),
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

  val variables = mutableMapOf<Identifier, Variable>()
  open val references = LinkedHashMap<Identifier, PlankType>()

  private val types = mutableMapOf<Identifier, PlankType>()
  private val expanded = mutableListOf<Scope>()

  fun expand(another: Scope): Scope {
    expanded += another

    return this
  }

  fun declare(name: Identifier, type: PlankType, location: Location, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, type.const().copy(location = location), this)
  }

  /**
   * Declares a compiler-defined variable with type [type] in the context
   */
  fun declare(name: Identifier, type: PlankType, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, type.const(), this)
  }

  fun declare(name: Identifier, value: TypedExpr, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, value, this)
  }

  fun getOrCreate(name: Identifier): PlankType {
    return types.getOrPut(name) {
      StructType(name)
    }
  }

  fun create(type: PlankType) {
    requireNotNull(type.name) { "type.name must be not null" }

    types[type.name] = type
  }

  fun create(name: Identifier, type: PlankType) {
    types[name] = type
  }

  fun findModule(name: Identifier): Module? {
    return moduleTree.findModule(name)
      ?: enclosing?.findModule(name)
  }

  fun findType(name: Identifier): PlankType? {
    return types[name]
      ?: enclosing?.findType(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findType(name) }
  }

  // todo add usage tracker
  @Suppress("UnusedPrivateMember")
  fun findVariableOn(scope: Scope, name: Identifier): Variable? {
    return variables[name]
  }

  fun findVariable(name: Identifier): Variable? {
    return variables[name]?.copy(isInScope = true)
      ?: enclosing?.findVariable(name)?.copy(isInScope = false)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariable(name) }
        ?.copy(isInScope = false)
  }

  fun findFunction(name: Identifier): FunctionType? {
    return findVariable(name)
      ?.value
      ?.type as? FunctionType
  }
}
