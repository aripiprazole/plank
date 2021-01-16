package com.lorenzoog.jplank.intellijplugin

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class PlankLanguageHighlighter : SyntaxHighlighterBase() {
  override fun getHighlightingLexer(): Lexer {
    return PlankLexerAdapter()
  }

  override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
    return when (tokenType) {
      PlankElementType.IF,
      PlankElementType.ELSE,
      PlankElementType.LET,
      PlankElementType.MUTABLE,
      PlankElementType.FUN,
      PlankElementType.NATIVE,
      PlankElementType.TYPE,
      PlankElementType.IMPORT,
      PlankElementType.SIZEOF,
      PlankElementType.TRUE,
      PlankElementType.FALSE,
      PlankElementType.SEMI -> arrayOf(KEYWORD)

      PlankElementType.VOID_TYPE,
      PlankElementType.INT_TYPE,
      PlankElementType.DOUBLE_TYPE,
      PlankElementType.STRING_TYPE,
      PlankElementType.BOOL_TYPE -> arrayOf(TYPE)

      PlankElementType.PRINTLN_FUNCTION,
      PlankElementType.PRINT_FUNCTION -> arrayOf(FUNCTION_CALL)

      PlankElementType.INT,
      PlankElementType.DOUBLE -> arrayOf(NUMBER)

      PlankElementType.IDENTIFIER -> arrayOf(VARIABLE)

      PlankElementType.STRING -> arrayOf(STRING)

      PlankElementType.EQUALS -> arrayOf(ASSIGN)

      PlankElementType.DOT,
      PlankElementType.STAR,
      PlankElementType.SLASH,
      PlankElementType.MINUS,
      PlankElementType.PLUS,
      PlankElementType.EQUALS_EQUALS,
      PlankElementType.BANG_EQUALS,
      PlankElementType.BANG,
      PlankElementType.GREATER,
      PlankElementType.GREATER_EQUALS,
      PlankElementType.LESS,
      PlankElementType.LESS_EQUALS,
      PlankElementType.AMPERSAND -> arrayOf(OPERATOR)

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
    val FUNCTION_CALL by attributesKey(DefaultLanguageHighlighterColors.FUNCTION_CALL)
    val TYPE by attributesKey(DefaultLanguageHighlighterColors.CLASS_REFERENCE)
    val BAD_CHARACTER by attributesKey(HighlighterColors.BAD_CHARACTER)
  }
}
