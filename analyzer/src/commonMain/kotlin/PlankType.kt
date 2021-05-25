package com.lorenzoog.plank.analyzer

sealed class PlankType {
  open val genericArity: Int = 0
  open val fields: List<Struct.Field> = emptyList()
  open val inherits: List<PlankType> = emptyList()
  open val isPrimitive = false

  abstract val size: Int

  val pointer by lazy { Pointer(this) }

  val isFP get() = this == Builtin.Double
  val isAny get() = this == Builtin.Any || this is Generic
  val isVoid get() = this == Builtin.Void
  val isGeneric get() = genericArity != 0

  inline fun <reified A : PlankType> cast(): A? {
    return when (this) {
      is A -> this
      is Delegate -> when (delegate) {
        is A -> delegate as A
        else -> null
      }
      else -> null
    }
  }

  operator fun get(name: String): Struct.Field? {
    return fields.find { it.name == name }
  }

  fun isAssignableBy(another: PlankType): Boolean {
    return this == Builtin.Any || this in another.inherits || this == another
  }

  data class Delegate(var delegate: PlankType? = null) : PlankType() {
    override val inherits get() = delegate!!.inherits
    override val size get() = delegate!!.size
    override val genericArity get() = delegate!!.genericArity
    override val isPrimitive get() = delegate!!.isPrimitive
    override val fields get() = delegate!!.fields

    override fun toString() = delegate!!.toString()

    override fun hashCode(): Int {
      var result = inherits.hashCode()
      result = 31 * result + size
      result = 31 * result + genericArity
      result = 31 * result + isPrimitive.hashCode()
      return result
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other == null || this::class != other::class) return false

      other as Delegate

      if (inherits != other.inherits) return false
      if (size != other.size) return false
      if (genericArity != other.genericArity) return false
      if (isPrimitive != other.isPrimitive) return false

      return true
    }
  }

  data class Generic(val receiver: PlankType, val arguments: List<PlankType>) : PlankType() {
    override val size get() = TODO("add size to generics")

    override fun toString(): String {
      return "($receiver (${arguments.joinToString()}))"
    }
  }

  data class Set(val name: String, val members: List<Member> = emptyList()) : PlankType() {
    data class Member(val name: String, val fields: List<PlankType>)

    fun findMember(name: String): Member? {
      return members.find { it.name == name }
    }

    override val size = 16

    override fun toString(): String {
      return "(set $name)"
    }
  }

  data class Module(
    val name: String,
    override val fields: List<Struct.Field> = emptyList(),
  ) : PlankType() {
    override val size = 0

    override fun toString(): String {
      return "(module $name)"
    }
  }

  data class Pointer(
    val inner: PlankType,
    override val fields: List<Struct.Field> = emptyList(),
  ) : PlankType() {
    override val isPrimitive: Boolean = true
    override val size = 8

    override fun toString(): String {
      return "(pointer $inner)"
    }
  }

  data class Array(val inner: PlankType) : PlankType() {
    override val isPrimitive: Boolean = true
    override val size get() = TODO("add size to arrays")

    override val fields: List<Struct.Field> = listOf(
      Struct.Field(mutable = false, name = "size", type = Builtin.Int)
    )

    override fun toString(): String {
      return "(array $inner)"
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

    override val size: Int = fields.fold(0) { acc, (_, _, type) ->
      acc + type.size
    }

    override fun toString(): String {
      return "(type $name)"
    }
  }

  data class Callable(val parameters: List<PlankType>, val returnType: PlankType) : PlankType() {
    override val isPrimitive: Boolean = true
    override val size = 8

    override fun toString(): String {
      return "(${parameters.joinToString()}) -> $returnType"
    }
  }
}
