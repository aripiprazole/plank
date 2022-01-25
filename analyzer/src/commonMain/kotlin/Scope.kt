package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.PlankFile

data class Variable(
  val mutable: Boolean,
  val name: Identifier,
  var value: TypedExpr,
  val declaredIn: Scope,
  val isInScope: Boolean = false,
) {
  override fun toString(): String {
    return "Variable(mutable=$mutable, name=$name, value=$value, isInScope=$isInScope)"
  }
}

class GlobalScope(override val moduleTree: ModuleTree) : Scope() {
  /**
   * Init compiler-defined functions
   */
  init {
    create(CharType)
    create(BoolType)
    create(IntType(32))
    create(FloatType(32))
    create(Identifier("Void"), UnitType)

    // Add default binary operators
    declare(Identifier.add(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.sub(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.times(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.div(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.div(), FunctionType(IntType(), IntType(), IntType()))

    // Add default logical operators
    declare(Identifier.eq(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.neq(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.gt(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.gte(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.lt(), FunctionType(IntType(), IntType(), IntType()))
    declare(Identifier.lte(), FunctionType(IntType(), IntType(), IntType()))
  }

  override val name = Identifier("Global")
  override val enclosing: Scope? = null
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

class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
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
