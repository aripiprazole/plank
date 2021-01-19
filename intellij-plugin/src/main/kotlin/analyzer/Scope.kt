package com.lorenzoog.jplank.intellijplugin.analyzer

import com.lorenzoog.jplank.analyzer.Variable
import com.lorenzoog.jplank.analyzer.type.PlankType

interface Scope {
  fun expand(another: Scope): Scope
  fun declare(name: String, type: PlankType, mutable: Boolean = false)
  fun define(name: String, type: PlankType.Struct)
  fun findType(name: String): PlankType?
  fun findStruct(name: String): PlankType.Struct?
  fun findVariable(name: String): Variable?
  fun findFunction(name: String): PlankType.Callable?
  fun lookup(): List<LookupResult>
}
