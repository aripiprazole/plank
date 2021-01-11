package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType

object Builtin {
  val Void = PkType.createStructure("Void")
  val Numeric = PkType.createStructure("Numeric")
  val Int = PkType.createStructure("Int", inherits = listOf(Numeric))
  val Double = PkType.createStructure("Float", inherits = listOf(Numeric))
  val Bool = PkType.createStructure("Bool")
  val Any = PkType.createStructure("Any")
  val String = PkType.createStructure("String")

  val values = mapOf(
    "Void" to Void,
    "Int" to Int,
    "Double" to Double,
    "Bool" to Bool,
    "Any" to Any,
    "String" to String
  )
}
