package com.lorenzoog.jplank.intellijplugin

import com.intellij.psi.tree.IElementType

class PlankElementType(private val name: String) : IElementType(name, Plank.INSTANCE) {
  override fun toString(): String {
    return "Plank.$name"
  }

  companion object PlankTypes {
    @JvmField
    val VOID_TYPE = PlankElementType("PLANK_VOID_TYPE")

    @JvmField
    val INT_TYPE = PlankElementType("PLANK_INT_TYPE")

    @JvmField
    val DOUBLE_TYPE = PlankElementType("PLANK_DOUBLE_TYPE")

    @JvmField
    val STRING_TYPE = PlankElementType("PLANK_STRING_TYPE")

    @JvmField
    val BOOL_TYPE = PlankElementType("PLANK_BOOL_TYPE")

    @JvmField
    val PRINTLN_FUNCTION = PlankElementType("PLANK_PRINTLN_FUNCTION")

    @JvmField
    val PRINT_FUNCTION = PlankElementType("PLANK_PRINT_FUNCTION")

    @JvmField
    val IMPORT = PlankElementType("PLANK_IMPORT")

    @JvmField
    val FUN = PlankElementType("PLANK_FUN")

    @JvmField
    val COMMA = PlankElementType("PLANK_COMMA")

    @JvmField
    val COLON = PlankElementType("PLANK_COLON")

    @JvmField
    val SEMI = PlankElementType("PLANK_SEMI")

    @JvmField
    val LET = PlankElementType("PLANK_LET")

    @JvmField
    val NATIVE = PlankElementType("PLANK_NATIVE")

    @JvmField
    val TYPE = PlankElementType("PLANK_TYPE")

    @JvmField
    val MUTABLE = PlankElementType("PLANK_MUTABLE")

    @JvmField
    val IF = PlankElementType("PLANK_IF")

    @JvmField
    val ELSE = PlankElementType("PLANK_ELSE")

    @JvmField
    val SIZEOF = PlankElementType("PLANK_SIZEOF")

    @JvmField
    val TRUE = PlankElementType("PLANK_TRUE")

    @JvmField
    val FALSE = PlankElementType("PLANK_FALSE")

    @JvmField
    val INT = PlankElementType("PLANK_INT")

    @JvmField
    val DOUBLE = PlankElementType("PLANK_DOUBLE")

    @JvmField
    val STRING = PlankElementType("PLANK_STRING")

    @JvmField
    val RPAREN = PlankElementType("PLANK_RPAREN")

    @JvmField
    val LPAREN = PlankElementType("PLANK_LPAREN")

    @JvmField
    val RBRACE = PlankElementType("PLANK_RBRACE")

    @JvmField
    val LBRACE = PlankElementType("PLANK_LBRACE")

    @JvmField
    val GREATER = PlankElementType("PLANK_GREATER")

    @JvmField
    val GREATER_EQUALS = PlankElementType("PLANK_GREATER_EQUALS")

    @JvmField
    val LESS_EQUALS = PlankElementType("PLANK_LESS_EQUALS")

    @JvmField
    val LESS = PlankElementType("PLANK_LESS")

    @JvmField
    val IDENTIFIER = PlankElementType("PLANK_IDENTIFIER")

    @JvmField
    val WS = PlankElementType("PLANK_WS")

    @JvmField
    val EOF = PlankElementType("PLANK_EOF")

    @JvmField
    val STAR = PlankElementType("PLANK_STAR")

    @JvmField
    val SLASH = PlankElementType("PLANK_SLASH")

    @JvmField
    val PLUS = PlankElementType("PLANK_PLUS")

    @JvmField
    val MINUS = PlankElementType("PLANK_MINUS")

    @JvmField
    val AMPERSAND = PlankElementType("PLANK_AMPERSAND")

    @JvmField
    val APOSTROPHE = PlankElementType("PLANK_APOSTROPHE")

    @JvmField
    val BANG = PlankElementType("PLANK_BANG")

    @JvmField
    val EQUALS = PlankElementType("PLANK_EQUAL")

    @JvmField
    val BANG_EQUALS = PlankElementType("PLANK_BANG_EQUAL")

    @JvmField
    val EQUALS_EQUALS = PlankElementType("PLANK_EQUAL_EQUAL")

    @JvmField
    val ARROW_LEFT = PlankElementType("PLANK_ARROW_LEFT")

    @JvmField
    val DOT = PlankElementType("PLANK_DOT")
  }
}
