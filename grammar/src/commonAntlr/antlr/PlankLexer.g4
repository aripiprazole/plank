lexer grammar PlankLexer;

WS : (' ' | '\t' | NEWLINE)+ -> channel(HIDDEN);
NEWLINE : ([\r\n] | [\n])+;

// symbols
SEMICOLON : ';'+;
COMMA : ',' ;
COLON : ':' ;

LPAREN : '(' ;
RPAREN : ')' ;

LBRACE : '{' ;
RBRACE : '}' ;

LBRACKET : '[' ;
RBRACKET : ']' ;

PLUS    : '+' ;
MINUS   : '-' ;
SLASH   : '/' ;
STAR   : '*' ;

BANG : '!' ;
EQUAL : '=' ;
GREATER : '>' ;
LESS : '<' ;

EQUAL_EQUAL : '==' ;
BANG_EQUAL : '!=' ;
GREATER_EQUAL : '>=' ;
LESS_EQUAL : '<=' ;

ARROW_LEFT : '->' ;

DOT : '.' ;

// keywords
RETURN : 'return' ;
FUN : 'fun' ;
TYPE : 'type' ;
LET : 'let' ;
IF : 'if' ;
ELSE : 'else' ;
MUTABLE : 'mutable' ;
TRUE : 'true' ;
FALSE : 'false' ;
IMPORT : 'import' ;
NATIVE : 'native' ;
SIZEOF : 'sizeof' ;

// identifiers
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*  ;

STRING : '"' (~["\r\n\\] | '\\' ~[\r\n])*  '"'
       | '\'' (~["\r\n\\] | '\\' ~[\r\n])*  '\''
       ;

INT     : [0-9]+ ;
DECIMAL     : INT '.' INT ;
