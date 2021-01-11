package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Stmt

fun Expr.getType(context: BindingContext): PkType {
  return context.visit(this)
}

fun Stmt.getType(context: BindingContext): PkType {
  return context.visit(this)
}
