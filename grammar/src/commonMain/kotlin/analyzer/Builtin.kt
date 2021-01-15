package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType

object Builtin {
  val Void: PkType = PkType.Struct("Void")
  val Numeric: PkType = PkType.Struct("Numeric")
  val Int: PkType = PkType.Struct("Int", inherits = listOf(Numeric), isPrimitive = true)
  val Double: PkType = PkType.Struct("Float", inherits = listOf(Numeric), isPrimitive = true)
  val Bool: PkType = PkType.Struct("Bool", isPrimitive = true)
  val Any: PkType = PkType.Struct("Any")
  val String: PkType = PkType.Struct("String")

  val values = mapOf(
    "Void" to Void,
    "Int" to Int,
    "Double" to Double,
    "Bool" to Bool,
    "String" to String
  )
}
