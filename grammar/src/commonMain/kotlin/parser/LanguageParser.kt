package com.lorenzoog.plank.grammar.parser

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.generated.PlankLexer

typealias LanguageLexer = PlankLexer

interface ExprParser {
  fun constExpr(): Expr.Const
  fun ifExpr(): Expr.If
  fun matchExpr(): Expr.Match
  fun groupExpr(): Expr.Group
  fun accessExpr(): Expr.Access
  fun assignExpr(): Expr.Assign
  fun setExpr(): Expr.Set
  fun getExpr(): Expr.Get
  fun callExpr(): Expr.Call
  fun instanceExpr(): Expr.Instance
  fun sizeofExpr(): Expr.Sizeof
  fun referenceExpr(): Expr.Reference
  fun valueExpr(): Expr.Value
}

interface DeclParser {
  fun letDecl(): Decl.LetDecl
  fun moduleDecl(): Decl.ModuleDecl
  fun importDecl(): Decl.ImportDecl
  fun funDecl(): Decl.FunDecl
  fun structDecl(): Decl.StructDecl
  fun enumDecl(): Decl.EnumDecl
}

interface StmtParser {
  fun exprStmt(): Stmt.ExprStmt
  fun returnStmt(): Stmt.ReturnStmt
}

interface LanguageParser : ExprParser, DeclParser

fun LanguageParser(code: String): LanguageParser = LanguageParserImpl()

private class LanguageParserImpl : LanguageParser
