package com.gabrielleeg1.plank.grammar.mapper

import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.PlankFile
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token

class SyntaxErrorListener(private val file: PlankFile) : BaseErrorListener() {
  private val _violations = mutableListOf<SyntaxViolation>()

  val violations: List<SyntaxViolation> get() = _violations

  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?
  ) {
    if (offendingSymbol is Token) {
      _violations += SyntaxViolation(msg, Location(offendingSymbol, file))
    }
  }
}
