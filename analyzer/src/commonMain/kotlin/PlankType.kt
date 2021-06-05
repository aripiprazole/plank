package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.analyzer.element.TypedConstExpr
import com.lorenzoog.plank.analyzer.element.TypedExpr
import com.lorenzoog.plank.analyzer.element.TypedInstanceExpr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location
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
 */
sealed class PlankType {
  open val name: Identifier? = null
  open val isPrimitive = false

  abstract val size: Int

  override fun toString(): String {
    return name?.text ?: "Type ${this::class.simpleName}@${hashCode().toString(4)} of size $size"
  }

  fun const(value: Any = Unit): TypedConstExpr {
    return TypedConstExpr(value, this, Location.undefined())
  }

  companion object {
    val unit = IntType("Void", 8)
    val char = IntType("Char", 8)
    val bool = IntType("Bool", 1)

    fun float(size: Int = 32, unsigned: Boolean = false): PlankType {
      return fpCache.getOrPut(size) {
        IntType("Float", size, floatingPoint = true, unsigned = unsigned)
      }
    }

    fun int(size: Int = 32, unsigned: Boolean = false): PlankType {
      return intCache.getOrPut(size) {
        IntType("Int", size, unsigned = unsigned)
      }
    }

    fun enum(name: Identifier, members: Map<Identifier, EnumMember> = emptyMap()): EnumType {
      return EnumType(name, members)
    }

    fun struct(
      name: Identifier,
      properties: Map<Identifier, StructProperty> = emptyMap()
    ): StructType {
      return StructType(name, properties)
    }

    fun pointer(type: PlankType): PointerType {
      return PointerType(type)
    }

    fun function(returnType: PlankType, parameters: List<PlankType>): FunctionType {
      return FunctionType(parameters, returnType)
    }

    fun function(returnType: PlankType, vararg parameters: PlankType): FunctionType {
      return FunctionType(parameters.toList(), returnType)
    }

    fun delegate(type: PlankType): DelegateType {
      return DelegateType(type)
    }

    fun module(name: Identifier, members: List<StructProperty>): ModuleType {
      return ModuleType(name, members)
    }

    fun array(type: PlankType): ArrayType {
      return ArrayType(type)
    }

    fun untyped(): Untyped {
      return Untyped
    }

    private val fpCache = mutableMapOf<Int, PlankType>()
    private val intCache = mutableMapOf<Int, PlankType>()
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

  inline fun <reified A : PlankType> cast(default: (PlankType) -> A): A {
    val type = cast<A>()

    return type ?: default(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as PlankType

    if (name != other.name) return false
    if (size != other.size) return false

    return true
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

class IntType(
  name: String,
  override val size: Int,
  val floatingPoint: Boolean = false,
  val unsigned: Boolean = false,
) : PlankType() {
  override val name = Identifier.of(name)
  override val isPrimitive: Boolean = true
}

// TODO: use currying
class FunctionType(val parameters: List<PlankType>, val returnType: PlankType) : PlankType() {
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

/**
 * Represents unknown type when compilers raises an violation or something
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
