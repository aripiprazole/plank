package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.PlankFile

data class Variable(val mutable: Boolean, val name: Identifier, var value: TypedExpr)

class GlobalScope(override val moduleTree: ModuleTree) : Scope() {
  /**
   * Init compiler-defined functions
   */
  init {
    create(UnitType)
    create(CharType)
    create(BoolType)
    create(IntType(32))
    create(FloatType(32))

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
) : Scope() {
  val parameters = function.parameters
  val returnType = function.returnType
}

class ClosureScope(
  override val name: Identifier,
  override val enclosing: Scope,
  override val moduleTree: ModuleTree = ModuleTree()
) : Scope()

sealed class Scope {
  abstract val name: Identifier
  abstract val enclosing: Scope?
  abstract val moduleTree: ModuleTree
  open val nested: Boolean get() = enclosing != null

  val variables = mutableMapOf<Identifier, Variable>()

  private val types = mutableMapOf<Identifier, PlankType>()
  private val expanded = mutableListOf<Scope>()

  fun expand(another: Scope): Scope {
    expanded += another

    return this
  }

  fun declare(name: Identifier, type: PlankType, location: Location, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, type.const().copy(location = location))
  }

  /**
   * Declares a compiler-defined variable with type [type] in the context
   */
  fun declare(name: Identifier, type: PlankType, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, type.const())
  }

  fun declare(name: Identifier, value: TypedExpr, mutable: Boolean = false) {
    variables[name] = Variable(mutable, name, value)
  }

  fun getOrCreate(name: Identifier): PlankType {
    return types.getOrPut(name) {
      StructType(name)
    }
  }

  fun create(type: PlankType) {
    requireNotNull(type.name) { "type.name must be not null" }

    types[type.name!!] = type
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
  fun findVariableOn(scope: Scope, name: Identifier): Variable? {
    return variables[name]
  }

  fun findVariable(name: Identifier): Variable? {
    return variables[name]
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariable(name) }
  }

  fun findFunction(name: Identifier): FunctionType? {
    return findVariable(name)
      ?.value
      ?.type as? FunctionType
  }
}
