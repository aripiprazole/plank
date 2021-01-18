package com.lorenzoog.jplank.intellijplugin.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.lorenzoog.jplank.intellijplugin.psi.PlankTypes;

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
GENERIC = \' {IDENTIFIER}

%state EOF

%%

<YYINITIAL> "fun" { return PlankTypes.FUN; }
<YYINITIAL> "import" { return PlankTypes.IMPORT; }
<YYINITIAL> "let" { return PlankTypes.LET; }
<YYINITIAL> "mutable" { return PlankTypes.MUTABLE; }
<YYINITIAL> "type" { return PlankTypes.TYPE; }
<YYINITIAL> "native" { return PlankTypes.NATIVE; }
<YYINITIAL> "if" { return PlankTypes.IF; }
<YYINITIAL> "else" { return PlankTypes.ELSE; }
<YYINITIAL> "sizeof" { return PlankTypes.SIZEOF; }
<YYINITIAL> "true" { return PlankTypes.TRUE; }
<YYINITIAL> "false" { return PlankTypes.FALSE; }
<YYINITIAL> "return" { return PlankTypes.RETURN; }
<YYINITIAL> "(" { return PlankTypes.LPAREN; }
<YYINITIAL> ")" { return PlankTypes.RPAREN; }
<YYINITIAL> "{" { return PlankTypes.LBRACE; }
<YYINITIAL> "}" { return PlankTypes.RBRACE; }
<YYINITIAL> ">" { return PlankTypes.GREATER; }
<YYINITIAL> ">=" { return PlankTypes.GREATER_EQUAL; }
<YYINITIAL> "<" { return PlankTypes.LESS; }
<YYINITIAL> "<=" { return PlankTypes.LESS_EQUAL; }
<YYINITIAL> "*" { return PlankTypes.STAR; }
<YYINITIAL> "++" { return PlankTypes.CONCAT; }
<YYINITIAL> "-" { return PlankTypes.MINUS; }
<YYINITIAL> "+" { return PlankTypes.PLUS; }
<YYINITIAL> "/" { return PlankTypes.SLASH; }
<YYINITIAL> "&" { return PlankTypes.AMPERSAND; }
<YYINITIAL> "=" { return PlankTypes.EQUAL; }
<YYINITIAL> "==" { return PlankTypes.EQUAL_EQUAL; }
<YYINITIAL> "!=" { return PlankTypes.BANG_EQUAL; }
<YYINITIAL> "." { return  PlankTypes.DOT; }
<YYINITIAL> "->" { return PlankTypes.ARROW_LEFT; }
<YYINITIAL> "," { return PlankTypes.COMMA; }
<YYINITIAL> ":" { return PlankTypes.COLON; }
<YYINITIAL> ";" { return PlankTypes.SEMICOLON; }
<YYINITIAL> {IDENTIFIER} { return PlankTypes.IDENTIFIER; }
<YYINITIAL> {STRING} { return PlankTypes.STRING; }
<YYINITIAL> {INT} { return PlankTypes.INT; }
<YYINITIAL> {DOUBLE} { return PlankTypes.DECIMAL; }
<YYINITIAL> {GENERIC} { return PlankTypes.IDENTIFIER; }
<YYINITIAL> <<EOF>> { yybegin(EOF); return PlankTypes.EOF; }
({NEWLINE}|{WS})+  { return PlankTypes.WS; }

[^] { return TokenType.BAD_CHARACTER; }

