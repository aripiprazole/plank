@file:Suppress("FunctionName")

package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
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
  abstract val name: Identifier
  open val isPrimitive = false

  abstract val size: Int

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
  val members: List<StructProperty> = emptyList(),
) : PlankType() {
  override val size = 0

  override fun toString(): String = "ModuleType(${name.text})"
}

data class PointerType(val inner: PlankType) : PlankType() {
  override val name: Identifier = Identifier("PointerType")
  override val isPrimitive: Boolean = true
  override val size = 8

  override fun toString(): String = "*$inner"
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

  override fun toString(): String = "StructType(${name.text}, size = $size)"
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

// TODO: use currying
class FunctionType(val parameters: List<PlankType>, val returnType: PlankType) :
  PlankType() {
  constructor(returnType: PlankType, parameters: List<PlankType>) : this(parameters, returnType)
  constructor(returnType: PlankType, vararg parameters: PlankType) : this(
    parameters.toList(),
    returnType,
  )

  override val name: Identifier = Identifier("FunctionType")

  override val isPrimitive: Boolean = true
  override val size = 8

  fun call(callee: TypedExpr, location: Location, arguments: List<TypedExpr>): TypedExpr {
    // TODO: add constant evaluation in compile-time if arguments are constants

    return TypedCallExpr(callee, arguments, returnType, location)
  }

  override fun toString(): String {
    return "${parameters.joinToString(" -> ")} -> $returnType"
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

  override fun toString() = "DelegateType(${value!!})"
}

val UnitType = IntType("Void", 8)
val CharType = IntType("Char", 8)
val BoolType = IntType("Bool", 1)

/**
 * Represents unknown type when compilers raise a violation or something
 */
object Untyped : PlankType() {
  override val name: Identifier = Identifier("Untyped")
  override val size: Int = -1

  override fun toString(): String = "Untyped"
}

data class EnumMember(val name: Identifier, val fields: List<PlankType>)

data class StructProperty(
  val mutable: Boolean,
  val name: Identifier,
  val type: PlankType,
  val value: TypedExpr? = null
)
