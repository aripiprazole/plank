package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeDef

interface BindingContext :
  Stmt.Visitor<PlankType>,
  Expr.Visitor<PlankType>,
  TypeDef.Visitor<PlankType>,
  PlankFile.Visitor<PlankType> {
  val violations: List<BindingViolation>
  val isValid: Boolean

  fun analyze(file: PlankFile): Boolean

  fun findScope(element: PlankElement): Scope?

  fun findCallee(expr: Expr): PlankType.Callable?
  fun findStructure(expr: Expr): PlankType.Struct?
}
