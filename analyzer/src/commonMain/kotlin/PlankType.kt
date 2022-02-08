@file:Suppress("FunctionName")

package org.plank.analyzer

import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import kotlin.reflect.KProperty

/**
 * Represents a type in plank's type-system
 *
 * @see DelegateType
 * @see StructType
 * @see FunctionType
 * @see EnumType
 * @see ArrayType
 * @see ModuleType
 * @see IntType
 * @see UnitType
 * @see CharType
 * @see BoolType
 */
sealed class PlankType {
  abstract val name: Identifier
  open val isPrimitive = false
  open val isNested = false
  open val isPartialApplied = false

  abstract val size: Int

  fun identity(): FunctionType {
    return FunctionType(this)
  }

  fun const(value: Any = Unit): TypedConstExpr {
    return TypedConstExpr(value, this, Location.Generated)
  }

  inline fun <reified A : PlankType> isInstance(): Boolean {
    return when (this) {
      is A -> true
      is DelegateType -> value is A
      else -> false
    }
  }

  inline fun <reified A : PlankType> cast(default: (PlankType) -> A): A {
    val type = cast<A>()

    return type ?: default(this)
  }

  inline fun <reified A : PlankType> unsafeCast(): A {
    return cast() ?: error("$this is not ${A::class.simpleName}")
  }

  inline fun <reified A : PlankType> cast(): A? {
    return when (this) {
      is A -> this
      is DelegateType -> when (value) {
        is A -> value as A
        else -> null
      }
      else -> null
    }
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    return when (other) {
      !is PlankType -> false
      is DelegateType -> other.value == this
      else -> name == other.name && size == other.size
    }
  }

  override fun toString(): String {
    return name.text
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + size
    return result
  }
}

data class EnumType(
  override val name: Identifier,
  val members: Map<Identifier, EnumMember> = emptyMap()
) : PlankType() {
  /**
   * Represents the size of tag got with [tag] and the pointer for the remaining struct
   */
  override val size = 16

  fun tag(name: Identifier): Int {
    return when (val index = members.values.indexOf(member(name))) {
      -1 -> -1
      else -> index + 1
    }
  }

  fun member(name: Identifier): EnumMember? {
    return members[name]
  }
}

class ModuleType(
  override val name: Identifier,
  private val members: List<StructProperty> = emptyList(),
) : PlankType() {
  override val size = 0

  fun property(name: Identifier): StructProperty? {
    return members.find { it.name == name }
  }

  override fun toString(): String = "(module ${name.text})"
}

data class PointerType(val inner: PlankType) : PlankType() {
  override val name: Identifier = Identifier("PointerType")
  override val isPrimitive: Boolean = true
  override val size = 8

  override fun toString(): String = when (inner) {
    is FunctionType -> "*($inner)"
    else -> "*$inner"
  }
}

data class ArrayType(val inner: PlankType) : PlankType() {
  override val name: Identifier = Identifier("ArrayType")
  override val isPrimitive: Boolean = true
  override val size get() = TODO("add size to arrays")

  override fun toString(): String = "[$inner]"
}

class StructType(
  override val name: Identifier,
  val properties: Map<Identifier, StructProperty> = emptyMap(),
) : PlankType() {
  fun property(name: Identifier): StructProperty? {
    return properties[name]
  }

  fun instantiate(location: Location, arguments: Map<Identifier, TypedExpr>): TypedExpr {
    // TODO: add constant evaluation in compile-time if arguments are constants

    return TypedInstanceExpr(arguments, this@StructType, location)
  }

  override val size: Int = properties.values.fold(0) { acc, property ->
    acc + property.type.size
  }

  override fun toString(): String = name.text
}

class IntType internal constructor(
  name: String,
  override val size: Int,
  val floatingPoint: Boolean = false,
  val unsigned: Boolean = false,
) : PlankType() {
  override val name = Identifier(name)
  override val isPrimitive: Boolean = true

  override fun toString(): String = buildString {
    append(name.text)
    append(if (floatingPoint) "f" else "")
    append(if (unsigned) "u" else "")
  }
}

private val floatCache = mutableMapOf<Int, IntType>()
private val intCache = mutableMapOf<Int, IntType>()

fun FloatType(size: Int = 32, unsigned: Boolean = false): IntType {
  return floatCache.getOrPut(size) {
    IntType(
      name = "Float$size",
      size,
      floatingPoint = true,
      unsigned = unsigned
    )
  }
}

fun IntType(size: Int = 32, unsigned: Boolean = false): IntType {
  return intCache.getOrPut(size) { IntType("Int$size", size, unsigned = unsigned) }
}

@Suppress("EqualsOrHashCode")
data class FunctionType(
  val parameter: PlankType,
  val returnType: PlankType,
  val actualReturnType: PlankType = returnType,
  val parameters: Map<Identifier, PlankType> = emptyMap(),
  val realParameters: Map<Identifier, PlankType> = parameters,
  override val name: Identifier = Identifier("undefined"),
  override val isNested: Boolean = false,
  override val isPartialApplied: Boolean = false,
  val references: Map<Identifier, PlankType> = emptyMap(),
) : PlankType() {
  fun nest(index: Int): PlankType {
    var i = 0
    var current = returnType

    while (index > i) {
      if (current is FunctionType) {
        current = current.returnType
      }
      i++
    }

    return current
  }

  override val isPrimitive: Boolean = true
  override val size = 8

  fun call(callee: TypedExpr, location: Location, arguments: List<TypedExpr>): TypedExpr {
    // TODO: add constant evaluation in compile-time if arguments are constants

    val returnType = when (returnType) {
      is FunctionType -> returnType.copy(
        realParameters = realParameters.entries.drop(arguments.size).associate { it.toPair() },
        isPartialApplied = true
      )
      else -> returnType
    }

    return TypedCallExpr(callee, arguments, returnType, location)
  }

  override fun equals(other: Any?): Boolean = super.equals(other)

  override fun toString(): String = buildString {
    append(
      when (parameter) {
        is FunctionType -> "($parameter)"
        is Untyped -> "()"
        else -> parameter.toString()
      }
    )
    append(" -> ")
    append(returnType)
  }
}

fun FunctionType(
  returnType: PlankType,
  parameters: List<PlankType>,
  realParameters: Map<Identifier, PlankType> = emptyMap(),
): FunctionType {
  val functionParameters = when {
    parameters.isEmpty() -> listOf(Untyped)
    else -> parameters.reversed()
  }

  return functionParameters.fold(returnType) { acc, parameter ->
    FunctionType(parameter, acc, realParameters = realParameters)
  } as? FunctionType ?: FunctionType(returnType, UnitType)
}

fun FunctionType(
  returnType: PlankType,
  vararg parameters: PlankType,
  realParameters: Map<Identifier, PlankType> = emptyMap(),
): FunctionType {
  return FunctionType(returnType, parameters.toList(), realParameters)
}

class DelegateType(var value: PlankType? = null) : PlankType() {
  override val name get() = value!!.name
  override val size get() = value!!.size
  override val isPrimitive get() = value!!.isPrimitive

  operator fun getValue(thisRef: Any?, property: KProperty<*>): PlankType {
    return value!!
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, plankType: PlankType) {
    value = plankType
  }

  override fun toString(): String = value!!.name.text // TODO: improve this
}

val UnitType = IntType("()", 8)
val CharType = IntType("Char", 8)
val BoolType = IntType("Bool", 1)

/**
 * Represents unknown type when compilers raise a violation or something
 */
object Untyped : PlankType() {
  override val name: Identifier = Identifier("???")
  override val size: Int = -1

  override fun toString(): String = "???"
}

data class EnumMember(
  val name: Identifier,
  val fields: List<PlankType>,
  val functionType: FunctionType,
)

data class StructProperty(
  val mutable: Boolean,
  val name: Identifier,
  val type: PlankType,
  val value: TypedExpr? = null
)
