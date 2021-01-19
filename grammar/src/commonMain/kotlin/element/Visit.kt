package com.lorenzoog.jplank.element

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PlankType
import kotlin.jvm.JvmName

@JvmName("visitStmts")
fun <T> Stmt.Visitor<T>.visit(many: List<Stmt>): List<T> = many.map(::visit)

@JvmName("visitExprs")
fun <T> Expr.Visitor<T>.visit(many: List<Expr>): List<T> = many.map(::visit)

@JvmName("visitTypeDefs")
fun <T> TypeDef.Visitor<T>.visit(many: List<TypeDef>): List<T> = many.map(::visit)

@JvmName("visitImports")
fun <T> ImportDirective.Visitor<T>.visit(many: List<ImportDirective>): List<T> = many.map(::visit)

@JvmName("visitPkTypeTypeDefNullable")
inline fun TypeDef.Visitor<PlankType>.visit(
  typeDef: TypeDef?,
  orElse: () -> PlankType = { Builtin.Void }
): PlankType {
  return typeDef?.let { visit(it) } ?: orElse()
}
