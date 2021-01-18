package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType

object Builtin {
  val Void: PkType = PkType.Struct("Void")
  val Numeric: PkType = PkType.Struct("Numeric", isPrimitive = true)
  val Int: PkType = PkType.Struct("Int", inherits = listOf(Numeric), isPrimitive = true)
  val Double: PkType = PkType.Struct("Double", inherits = listOf(Numeric), isPrimitive = true)
  val Bool: PkType = PkType.Struct("Bool", isPrimitive = true)
  val Any: PkType = PkType.Struct("Any")
  val String: PkType = PkType.Struct("String")
  val Char: PkType = PkType.Struct("Char")

  val values = mapOf(
    "Void" to Void,
    "Int" to Int,
    "Double" to Double,
    "Bool" to Bool,
    "Char" to Char
  )
}
