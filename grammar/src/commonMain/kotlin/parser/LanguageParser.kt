package com.gabrielleeg1.plank.grammar.parser

import com.gabrielleeg1.plank.grammar.element.AccessExpr
import com.gabrielleeg1.plank.grammar.element.AssignExpr
import com.gabrielleeg1.plank.grammar.element.CallExpr
import com.gabrielleeg1.plank.grammar.element.ConstExpr
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.grammar.element.DerefExpr
import com.gabrielleeg1.plank.grammar.element.EnumDecl
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.ExprStmt
import com.gabrielleeg1.plank.grammar.element.FunDecl
import com.gabrielleeg1.plank.grammar.element.GetExpr
import com.gabrielleeg1.plank.grammar.element.GroupExpr
import com.gabrielleeg1.plank.grammar.element.IfExpr
import com.gabrielleeg1.plank.grammar.element.ImportDecl
import com.gabrielleeg1.plank.grammar.element.InstanceExpr
import com.gabrielleeg1.plank.grammar.element.LetDecl
import com.gabrielleeg1.plank.grammar.element.MatchExpr
import com.gabrielleeg1.plank.grammar.element.ModuleDecl
import com.gabrielleeg1.plank.grammar.element.RefExpr
import com.gabrielleeg1.plank.grammar.element.ReturnStmt
import com.gabrielleeg1.plank.grammar.element.SetExpr
import com.gabrielleeg1.plank.grammar.element.SizeofExpr
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.grammar.element.StructDecl
import com.gabrielleeg1.plank.grammar.generated.PlankLexer

typealias LanguageLexer = PlankLexer

interface ExprParser {
  fun constExpr(): ConstExpr
  fun ifExpr(): IfExpr
  fun matchExpr(): MatchExpr
  fun groupExpr(): GroupExpr
  fun accessExpr(): AccessExpr
  fun assignExpr(): AssignExpr
  fun setExpr(): SetExpr
  fun getExpr(): GetExpr
  fun callExpr(): CallExpr
  fun instanceExpr(): InstanceExpr
  fun sizeofExpr(): SizeofExpr
  fun refExpr(): RefExpr
  fun derefExpr(): DerefExpr
}

interface DeclParser {
  fun letDecl(): LetDecl
  fun moduleDecl(): ModuleDecl
  fun importDecl(): ImportDecl
  fun funDecl(): FunDecl
  fun structDecl(): StructDecl
  fun enumDecl(): EnumDecl
}

interface StmtParser {
  fun exprStmt(): ExprStmt
  fun returnStmt(): ReturnStmt
}

interface LanguageParser : ExprParser, DeclParser

fun LanguageParser(code: String): LanguageParser = LanguageParserImpl()

private class LanguageParserImpl : LanguageParser {
  override fun constExpr(): ConstExpr {
    TODO("Not yet implemented")
  }

  override fun ifExpr(): IfExpr {
    TODO("Not yet implemented")
  }

  override fun matchExpr(): MatchExpr {
    TODO("Not yet implemented")
  }

  override fun groupExpr(): GroupExpr {
    TODO("Not yet implemented")
  }

  override fun accessExpr(): AccessExpr {
    TODO("Not yet implemented")
  }

  override fun assignExpr(): AssignExpr {
    TODO("Not yet implemented")
  }

  override fun setExpr(): SetExpr {
    TODO("Not yet implemented")
  }

  override fun getExpr(): GetExpr {
    TODO("Not yet implemented")
  }

  override fun callExpr(): CallExpr {
    TODO("Not yet implemented")
  }

  override fun instanceExpr(): InstanceExpr {
    TODO("Not yet implemented")
  }

  override fun sizeofExpr(): SizeofExpr {
    TODO("Not yet implemented")
  }

  override fun refExpr(): RefExpr {
    TODO("Not yet implemented")
  }

  override fun derefExpr(): DerefExpr {
    TODO("Not yet implemented")
  }

  override fun letDecl(): LetDecl {
    TODO("Not yet implemented")
  }

  override fun moduleDecl(): ModuleDecl {
    TODO("Not yet implemented")
  }

  override fun importDecl(): ImportDecl {
    TODO("Not yet implemented")
  }

  override fun funDecl(): FunDecl {
    TODO("Not yet implemented")
  }

  override fun structDecl(): StructDecl {
    TODO("Not yet implemented")
  }

  override fun enumDecl(): EnumDecl {
    TODO("Not yet implemented")
  }
}
