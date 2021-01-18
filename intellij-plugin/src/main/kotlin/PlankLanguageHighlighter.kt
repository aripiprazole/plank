package com.lorenzoog.jplank.intellijplugin

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.lorenzoog.jplank.intellijplugin.lexer.PlankLexerAdapter
import com.lorenzoog.jplank.intellijplugin.psi.PlankTypes

class PlankLanguageHighlighter : SyntaxHighlighterBase() {
  override fun getHighlightingLexer(): Lexer {
    return PlankLexerAdapter()
  }

  override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
    return when (tokenType) {
      PlankTypes.IF,
      PlankTypes.ELSE,
      PlankTypes.LET,
      PlankTypes.MUTABLE,
      PlankTypes.FUN,
      PlankTypes.NATIVE,
      PlankTypes.TYPE,
      PlankTypes.IMPORT,
      PlankTypes.SIZEOF,
      PlankTypes.TRUE,
      PlankTypes.FALSE,
      PlankTypes.RETURN,
      PlankTypes.SEMICOLON -> arrayOf(KEYWORD)

      PlankTypes.INT,
      PlankTypes.DECIMAL -> arrayOf(NUMBER)

      PlankTypes.IDENTIFIER -> arrayOf(VARIABLE)

      PlankTypes.STRING -> arrayOf(STRING)

      PlankTypes.EQUAL -> arrayOf(ASSIGN)

      PlankTypes.DOT,
      PlankTypes.STAR,
      PlankTypes.SLASH,
      PlankTypes.MINUS,
      PlankTypes.PLUS,
      PlankTypes.EQUAL_EQUAL,
      PlankTypes.BANG_EQUAL,
      PlankTypes.BANG,
      PlankTypes.GREATER,
      PlankTypes.GREATER_EQUAL,
      PlankTypes.LESS,
      PlankTypes.LESS_EQUAL,
      PlankTypes.AMPERSAND -> arrayOf(OPERATOR)

      TokenType.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)

      else -> emptyArray()
    }
  }

  companion object {
    val KEYWORD by attributesKey(DefaultLanguageHighlighterColors.KEYWORD)
    val NUMBER by attributesKey(DefaultLanguageHighlighterColors.NUMBER)
    val ASSIGN by attributesKey(DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val OPERATOR by attributesKey(DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val STRING by attributesKey(DefaultLanguageHighlighterColors.STRING)
    val VARIABLE by attributesKey(DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
    val BAD_CHARACTER by attributesKey(HighlighterColors.BAD_CHARACTER)
  }
}
