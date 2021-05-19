package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.analyzer.PlankType.Struct

object Builtin {
  val Void: PlankType = Struct("Void")
  val Numeric: PlankType = Struct("Numeric", isPrimitive = true)
  val Int: PlankType = Struct("Int", inherits = listOf(Numeric), isPrimitive = true)
  val Double: PlankType = Struct("Double", inherits = listOf(Numeric), isPrimitive = true)
  val Bool: PlankType = Struct("Bool", isPrimitive = true)
  val Any: PlankType = Struct("Any")
  val String: PlankType = Struct("String")
  val Char: PlankType = Struct("Char")

  val values = mapOf(
    "Void" to Void,
    "Int" to Int,
    "Double" to Double,
    "Bool" to Bool,
    "Char" to Char
  )
}
