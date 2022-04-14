package org.plank.syntax

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ConsoleErrorListener
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.atn.PredictionMode
import org.plank.parser.PlankLexer
import org.plank.parser.PlankParser
import org.plank.syntax.element.PlankFile

fun stubFile(): PlankFile {
  return PlankFile("")
}

fun testParsing(code: String): PlankParser {
  val lexer = PlankLexer(CharStreams.fromString(code))
  val parser = PlankParser(CommonTokenStream(lexer))

  parser.interpreter?.apply {
    predictionMode = PredictionMode.SLL
  }

  parser.addErrorListener(ConsoleErrorListener.INSTANCE)
  parser.addErrorListener(DiagnosticErrorListener())

  return parser
}
