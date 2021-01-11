package com.lorenzoog.jplank.analyzer.type

import com.lorenzoog.jplank.analyzer.Builtin

sealed class PkType : TypeCompanion {
  open val inherits: List<PkType> = emptyList()

  val isVoid get() = this == Builtin.Void

  override fun isAssignableBy(another: PkType): Boolean {
    return this == Builtin.Any || this in another.inherits || this == another
  }

  companion object {
    fun createArray(inner: PkType): PkArray {
      return PkArray(inner)
    }

    fun createStructure(
      name: String,
      fields: List<PkStructure.Field> = emptyList(),
      inherits: List<PkType> = emptyList()
    ): PkStructure {
      return PkStructure(name, fields, inherits)
    }

    fun createCallable(parameters: List<PkType>, returnType: PkType): PkCallable {
      return PkCallable(parameters, returnType)
    }
  }
}

data class PkArray(val inner: PkType) : PkType() {
  override fun toString(): String {
    return "[$inner]"
  }
}

data class PkStructure(
  val name: String = "null",
  val fields: List<Field>,
  override val inherits: List<PkType>,
) : PkType() {
  data class Field(val mutable: Boolean, val name: String, val type: PkType)

  operator fun get(name: String): Field? {
    return fields.find { it.name == name }
  }

  override fun toString(): String {
    return name
  }
}

data class PkCallable(val parameters: List<PkType>, val returnType: PkType) : PkType() {
  override fun toString(): String {
    return "${parameters.joinToString(prefix = "(", postfix = ")")} -> $returnType"
  }
}
