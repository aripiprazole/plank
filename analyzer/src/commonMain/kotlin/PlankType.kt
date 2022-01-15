@file:Suppress("FunctionName")

package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.element.TypedConstExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.Location
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
  open val name: Identifier? = null
  open val isPrimitive = false

  abstract val size: Int

  fun const(value: Any = Unit): TypedConstExpr {
    return TypedConstExpr(value, this, Location.Generated)
  }

  inline fun <reified A : PlankType> cast(default: (PlankType) -> A): A {
    val type = cast<A>()

    return type ?: default(this)
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
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as PlankType

    if (name != other.name) return false
    if (size != other.size) return false

    return true
  }

  override fun toString(): String {
    return name?.text ?: "Type ${this::class.simpleName}@${hashCode().toString(4)} of size $size"
  }

  override fun hashCode(): Int {
    var result = name?.hashCode() ?: 0
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
  val members: List<StructProperty> = emptyList(),
) : PlankType() {
  override val size = 0
}

data class PointerType(val inner: PlankType) : PlankType() {
  override val isPrimitive: Boolean = true
  override val size = 8

  override fun toString(): String {
    return "&$inner"
  }
}

data class ArrayType(val inner: PlankType) : PlankType() {
  override val isPrimitive: Boolean = true
  override val size get() = TODO("add size to arrays")
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
}

class IntType internal constructor(
  name: String,
  override val size: Int,
  val floatingPoint: Boolean = false,
  val unsigned: Boolean = false,
) : PlankType() {
  override val name = Identifier(name)
  override val isPrimitive: Boolean = true
}

private val floatCache = mutableMapOf<Int, IntType>()
private val intCache = mutableMapOf<Int, IntType>()

fun FloatType(size: Int = 32, unsigned: Boolean = false): IntType {
  return floatCache.getOrPut(size) {
    IntType(
      "Float",
      size,
      floatingPoint = true,
      unsigned = unsigned
    )
  }
}

fun IntType(size: Int = 32, unsigned: Boolean = false): IntType {
  return intCache.getOrPut(size) { IntType("Int", size, unsigned = unsigned) }
}

// TODO: use currying
class FunctionType(val parameters: List<PlankType>, val returnType: PlankType) :
  PlankType() {
  constructor(returnType: PlankType, parameters: List<PlankType>) : this(parameters, returnType)
  constructor(returnType: PlankType, vararg parameters: PlankType) : this(
    parameters.toList(),
    returnType,
  )

  override val isPrimitive: Boolean = true
  override val size = 8

  fun call(arguments: List<TypedExpr>): TypedExpr {
    // TODO: add constant evaluation in compile-time if arguments are constants

    return returnType.const()
  }

  override fun toString(): String {
    return "${parameters.joinToString("->")} -> $returnType"
  }
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

  override fun toString() = value!!.toString()
}

val UnitType = IntType("Void", 8)
val CharType = IntType("Char", 8)
val BoolType = IntType("Bool", 1)

/**
 * Represents unknown type when compilers raise a violation or something
 */
object Untyped : PlankType() {
  override val size: Int = -1
}

data class EnumMember(val name: Identifier, val fields: List<PlankType>)

data class StructProperty(
  val mutable: Boolean,
  val name: Identifier,
  val type: PlankType,
  val value: TypedExpr? = null
)
