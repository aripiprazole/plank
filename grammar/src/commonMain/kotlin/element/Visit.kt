package com.lorenzoog.plank.grammar.element

import kotlin.jvm.JvmName

@JvmName("visitStmts")
fun <T> Stmt.Visitor<T>.visit(many: List<Stmt>): List<T> = many.map(::visit)

@JvmName("visitExprs")
fun <T> Expr.Visitor<T>.visit(many: List<Expr>): List<T> = many.map(::visit)

@JvmName("visitTypeDefs")
fun <T> TypeDef.Visitor<T>.visit(many: List<TypeDef>): List<T> = many.map(::visit)
