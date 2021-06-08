package com.lorenzoog.plank.analyzer.element

import kotlin.jvm.JvmName

@JvmName("visitTypedStmts")
fun <T> ResolvedStmt.Visitor<T>.visit(many: List<ResolvedStmt>): List<T> = many.map(::visit)

@JvmName("visitTypedTypedExprs")
fun <T> TypedExpr.Visitor<T>.visit(many: List<TypedExpr>): List<T> = many.map(::visit)
