package com.lorenzoog.jplank.intellijplugin

import com.intellij.lexer.FlexAdapter

class PlankLexerAdapter : FlexAdapter(IdeaPlankLexer(null))
