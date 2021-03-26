package com.lorenzoog.jplank.analyzer.type

import com.lorenzoog.jplank.analyzer.Builtin

sealed class PlankType {
  open val genericArity: Int = 0
  open val fields: List<Struct.Field> = emptyList()
  open val inherits: List<PlankType> = emptyList()
  open val isPrimitive = false

  val pointer by lazy { Pointer(this) }

  val isFP get() = this == Builtin.Double
  val isAny get() = this == Builtin.Any || this is Generic
  val isVoid get() = this == Builtin.Void
  val isGeneric get() = genericArity != 0

  operator fun get(name: String): Struct.Field? {
    return fields.find { it.name == name }
  }

  fun isAssignableBy(another: PlankType): Boolean {
    return this == Builtin.Any || this in another.inherits || this == another
  }

  data class Generic(val receiver: PlankType, val arguments: List<PlankType>) : PlankType() {
    override fun toString(): String {
      return "$receiver<${arguments.joinToString()}>"
    }
  }

  data class Module(
    val name: String,
    override val fields: List<Struct.Field> = emptyList(),
  ) : PlankType() {
    override fun toString(): String {
      return "module $name"
    }
  }

  data class Pointer(
    val inner: PlankType,
    override val fields: List<Struct.Field> = emptyList(),
  ) : PlankType() {
    override val isPrimitive: Boolean = true

    override fun toString(): String {
      return "*$inner"
    }
  }

  data class Array(val inner: PlankType) : PlankType() {
    override val isPrimitive: Boolean = true

    override val fields: List<Struct.Field> = listOf(
      Struct.Field(mutable = false, name = "size", type = Builtin.Int)
    )

    override fun toString(): String {
      return "[$inner]"
    }
  }

  data class Struct(
    val name: String = "null",
    override val fields: List<Field> = emptyList(),
    override val inherits: List<PlankType> = emptyList(),
    override val genericArity: Int = 0,
    override val isPrimitive: Boolean = false,
  ) : PlankType() {
    data class Field(val mutable: Boolean, val name: String, val type: PlankType)

    override fun toString(): String {
      return "struct $name"
    }
  }

  data class Callable(val parameters: List<PlankType>, val returnType: PlankType) : PlankType() {
    override val isPrimitive: Boolean = true

    override fun toString(): String {
      return "(${parameters.joinToString()}) -> $returnType"
    }
  }
}
