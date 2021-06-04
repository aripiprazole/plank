package com.lorenzoog.plank.analyzer.element

import kotlin.jvm.JvmName

@JvmName("visitTypedStmts")
fun <T> TypedStmt.Visitor<T>.visit(many: List<TypedStmt>): List<T> = many.map(::visit)

@JvmName("visitTypedTypedExprs")
fun <T> TypedExpr.Visitor<T>.visit(many: List<TypedExpr>): List<T> = many.map(::visit)
