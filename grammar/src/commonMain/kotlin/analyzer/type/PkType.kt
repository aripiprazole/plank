package com.lorenzoog.jplank.analyzer.type

import com.lorenzoog.jplank.analyzer.Builtin

sealed class PkType : TypeCompanion {
  open val genericArity: Int = 0
  open val fields: List<Struct.Field> = emptyList()
  open val inherits: List<PkType> = emptyList()
  open val isPrimitive = false

  val pointer by lazy { Pointer(this) }

  val isFP get() = this == Builtin.Double
  val isAny get() = this == Builtin.Any || this is Generic
  val isVoid get() = this == Builtin.Void
  val isGeneric get() = genericArity != 0

  override fun isAssignableBy(another: PkType): Boolean {
    return this == Builtin.Any || this in another.inherits || this == another
  }

  data class Generic(val receiver: PkType, val arguments: List<PkType>) : PkType() {
    override fun toString(): String {
      return "$receiver<${arguments.joinToString()}>"
    }
  }

  data class Pointer(val inner: PkType) : PkType() {
    override val isPrimitive: Boolean = true

    override fun toString(): String {
      return "*$inner"
    }
  }

  data class Array(val inner: PkType) : PkType() {
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
    override val inherits: List<PkType> = emptyList(),
    override val genericArity: Int = 0,
    override val isPrimitive: Boolean = false,
  ) : PkType() {
    data class Field(val mutable: Boolean, val name: String, val type: PkType)

    operator fun get(name: String): Field? {
      return fields.find { it.name == name }
    }

    override fun toString(): String {
      return name
    }
  }

  data class Callable(val parameters: List<PkType>, val returnType: PkType) : PkType() {
    override val isPrimitive: Boolean = true

    override fun toString(): String {
      return "(${parameters.joinToString()}) -> $returnType"
    }
  }
}
