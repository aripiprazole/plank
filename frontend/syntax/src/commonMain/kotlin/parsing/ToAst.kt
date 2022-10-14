package org.plank.syntax.parsing

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.parser.PlankParser.FileContext
import org.plank.syntax.parser.PlankParser.ModuleContext
import org.plank.syntax.parser.PlankParser.QualifiedPathContext

fun FileContext.fileToAst(file: PlankFile): PlankFile {
  return file.copy(
    moduleName = findModule()?.moduleToAst(file),
    program = findDecl().map { it.declToAst(file) },
  )
}

fun Identifier.idToExpr(): AccessExpr = AccessExpr(this, null, loc)

fun ModuleContext.moduleToAst(file: PlankFile): QualifiedPath = path!!.pathToAst(file)

fun ParserRuleContext.treeLoc(file: PlankFile): Loc =
  Loc(start!!.startIndex, stop!!.stopIndex, file)

fun Interval.treeLoc(file: PlankFile): Loc =
  Loc(a, b, file)

fun TerminalNode.terminalToAst(file: PlankFile): Identifier =
  Identifier(text, sourceInterval.treeLoc(file))

fun QualifiedPathContext.pathToAst(file: PlankFile): QualifiedPath =
  QualifiedPath(text, treeLoc(file))

fun Token.tokenToAst(file: PlankFile): Identifier {
  val text = text ?: error("No text received in Token")

  return Identifier(text, Loc(startIndex, stopIndex, file))
}
