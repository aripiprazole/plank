package com.lorenzoog.jplank.intellijplugin.lexer

import com.intellij.lexer.FlexAdapter

class PlankLexerAdapter : FlexAdapter(IdeaPlankLexer(null))
