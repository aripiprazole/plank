package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.element.Stmt
import com.lorenzoog.jplank.element.TypeDef

interface BindingContext :
  Stmt.Visitor<PlankType>,
  Expr.Visitor<PlankType>,
  TypeDef.Visitor<PlankType>,
  PlankFile.Visitor<PlankType> {
  val violations: List<BindingViolation>
  val isValid: Boolean

  fun analyze(file: PlankFile): Boolean

  fun findScope(expr: Expr): Scope?

  fun findCallee(expr: Expr): PlankType.Callable?
  fun findStructure(expr: Expr): PlankType.Struct?
}
