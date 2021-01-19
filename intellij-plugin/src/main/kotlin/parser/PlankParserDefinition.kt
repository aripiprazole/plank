package com.lorenzoog.jplank.intellijplugin.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.lorenzoog.jplank.intellijplugin.Plank
import com.lorenzoog.jplank.intellijplugin.lexer.PlankLexerAdapter
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile
import com.lorenzoog.jplank.intellijplugin.psi.PlankTypes

class PlankParserDefinition : ParserDefinition {
  override fun createLexer(project: Project?): Lexer {
    return PlankLexerAdapter()
  }

  override fun createParser(project: Project?): PsiParser {
    return PlankParser()
  }

  override fun getFileNodeType(): IFileElementType {
    return IFileElementType(Plank)
  }

  override fun getWhitespaceTokens(): TokenSet {
    return WS
  }

  override fun getCommentTokens(): TokenSet {
    return COMMENTS
  }

  override fun getStringLiteralElements(): TokenSet {
    return STRINGS
  }

  override fun createElement(node: ASTNode?): PsiElement {
    return PlankTypes.Factory.createElement(node)
  }

  override fun createFile(viewProvider: FileViewProvider): PsiFile {
    return PlankPsiFile(viewProvider)
  }

  companion object {
    private val STRINGS = TokenSet.create(PlankTypes.STRING)
    private val WS = TokenSet.create(PlankTypes.WS)
    private val COMMENTS = TokenSet.EMPTY
  }
}
