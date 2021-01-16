package com.lorenzoog.jplank.intellijplugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.lorenzoog.jplank.intellijplugin.PlankElementType;

%%

%class IdeaPlankLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

NEWLINE = (\r\n|\n|\r)
WS = [ \t\f]+
STRING = \" ([^\"\\\n\r]|\\[^\n\r])* \"
INT = [0-9]+
DOUBLE = {INT} . {INT}
IDENTIFIER = [_a-zA-Z][_a-zA-Z0-9]*

%state EOF

%%

<YYINITIAL> "fun" { return PlankElementType.FUN; }

<YYINITIAL> "import" { return PlankElementType.IMPORT; }

<YYINITIAL> "let" { return PlankElementType.LET; }

<YYINITIAL> "mutable" { return PlankElementType.MUTABLE; }

<YYINITIAL> "type" { return PlankElementType.TYPE; }

<YYINITIAL> "type" { return PlankElementType.TYPE; }

<YYINITIAL> "native" { return PlankElementType.NATIVE; }

<YYINITIAL> "if" { return PlankElementType.IF; }

<YYINITIAL> "else" { return PlankElementType.ELSE; }

<YYINITIAL> "sizeof" { return PlankElementType.SIZEOF; }

<YYINITIAL> "true" { return PlankElementType.TRUE; }

<YYINITIAL> "false" { return PlankElementType.FALSE; }

<YYINITIAL> "Void" { return PlankElementType.VOID_TYPE; }

<YYINITIAL> "Int" { return PlankElementType.INT_TYPE; }

<YYINITIAL> "Double" { return PlankElementType.DOUBLE_TYPE; }

<YYINITIAL> "Bool" { return PlankElementType.BOOL_TYPE; }

<YYINITIAL> "String" { return PlankElementType.STRING_TYPE; }

<YYINITIAL> "println" { return PlankElementType.PRINTLN_FUNCTION; }

<YYINITIAL> "print" { return PlankElementType.PRINT_FUNCTION; }

<YYINITIAL> "(" { return PlankElementType.RPAREN; }

<YYINITIAL> ")" { return PlankElementType.LPAREN; }

<YYINITIAL> "{" { return PlankElementType.LBRACE; }

<YYINITIAL> "}" { return PlankElementType.RBRACE; }

<YYINITIAL> ">" { return PlankElementType.GREATER; }

<YYINITIAL> ">=" { return PlankElementType.GREATER_EQUALS; }

<YYINITIAL> "<" { return PlankElementType.LESS; }

<YYINITIAL> "<=" { return PlankElementType.LESS_EQUALS; }

<YYINITIAL> "*" { return PlankElementType.STAR; }

<YYINITIAL> "-" { return PlankElementType.MINUS; }

<YYINITIAL> "+" { return PlankElementType.PLUS; }

<YYINITIAL> "/" { return PlankElementType.SLASH; }

<YYINITIAL> "&" { return PlankElementType.AMPERSAND; }

<YYINITIAL> "=" { return PlankElementType.EQUALS; }

<YYINITIAL> "==" { return PlankElementType.EQUALS_EQUALS; }

<YYINITIAL> "!=" { return PlankElementType.BANG_EQUALS; }

<YYINITIAL> "." { return PlankElementType.DOT; }

<YYINITIAL> "->" { return PlankElementType.ARROW_LEFT; }

<YYINITIAL> "," { return PlankElementType.COMMA; }

<YYINITIAL> ":" { return PlankElementType.COLON; }

<YYINITIAL> ";" { return PlankElementType.SEMI; }

<YYINITIAL> {IDENTIFIER} { return PlankElementType.IDENTIFIER; }

<YYINITIAL> {STRING} { return PlankElementType.STRING; }

<YYINITIAL> {INT} { return PlankElementType.INT; }

<YYINITIAL> {DOUBLE} { return PlankElementType.DOUBLE; }

<YYINITIAL> <<EOF>> { yybegin(EOF); return PlankElementType.EOF; }

({NEWLINE}|{WS})+  { return PlankElementType.WS; }

[^] { return TokenType.BAD_CHARACTER; }

