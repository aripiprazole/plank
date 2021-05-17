package com.lorenzoog.plank.analyzer

object Builtin {
  val Void: PlankType = PlankType.Struct("Void")
  val Numeric: PlankType = PlankType.Struct("Numeric", isPrimitive = true)
  val Int: PlankType = PlankType.Struct("Int", inherits = listOf(Numeric), isPrimitive = true)
  val Double: PlankType = PlankType.Struct("Double", inherits = listOf(Numeric), isPrimitive = true)
  val Bool: PlankType = PlankType.Struct("Bool", isPrimitive = true)
  val Any: PlankType = PlankType.Struct("Any")
  val String: PlankType = PlankType.Struct("String")
  val Char: PlankType = PlankType.Struct("Char")

  val values = mapOf(
    "Void" to Void,
    "Int" to Int,
    "Double" to Double,
    "Bool" to Bool,
    "Char" to Char
  )
}
