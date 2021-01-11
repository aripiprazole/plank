package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkCallable
import com.lorenzoog.jplank.analyzer.type.PkStructure
import com.lorenzoog.jplank.analyzer.type.PkType
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.ImportDirective
import com.lorenzoog.jplank.element.PkFile
import com.lorenzoog.jplank.element.Stmt
import com.lorenzoog.jplank.element.TypeDef

interface BindingContext :
  Stmt.Visitor<PkType>,
  Expr.Visitor<PkType>,
  TypeDef.Visitor<PkType>,
  ImportDirective.Visitor<PkType> {
  val violations: List<BindingViolation>
  val isValid: Boolean

  fun analyze(file: PkFile): Boolean

  fun getScope(expr: Expr): Scope?

  fun findCallee(expr: Expr): PkCallable?
  fun findStructure(expr: Expr): PkStructure?
}
