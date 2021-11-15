package com.gabrielleeg1.plank.grammar.element

import kotlin.jvm.JvmName

@JvmName("visitStmts")
fun <T> Stmt.Visitor<T>.visit(many: List<Stmt>): List<T> = many.map(::visit)

@JvmName("visitExprs")
fun <T> Expr.Visitor<T>.visit(many: List<Expr>): List<T> = many.map(::visit)

@JvmName("visitPatterns")
fun <T> Pattern.Visitor<T>.visit(many: List<Pattern>): List<T> = many.map(::visit)

@JvmName("visitTypeReferences")
fun <T> TypeRef.Visitor<T>.visit(many: List<TypeRef>): List<T> = many.map(::visit)
