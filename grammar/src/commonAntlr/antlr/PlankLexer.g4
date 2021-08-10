lexer grammar PlankLexer;

WS : (' ' | '\t' | NEWLINE)+ -> channel(HIDDEN);
NEWLINE : ([\r\n] | [\n])+;

// symbols
SEMICOLON : ';'+;
COMMA : ',' ;
COLON : ':' ;

BAR : '|' ;

LPAREN : '(' ;
RPAREN : ')' ;

LBRACE : '{' ;
RBRACE : '}' ;

LBRACKET: '[' ;
RBRACKET: ']' ;

ADD: '+';
SUB: '-';
DIV: '/';
TIMES: '*';
CONCAT: '++';

AMPERSTAND: '&';

BANG: '!';
ASSIGN: ':=';
EQUAL: '=';

EQ: '==';
NEQ: '!=';
GTE: '>=';
GT: '>';
LT: '<';
LTE: '<=';

APOSTROPHE: '\'';

DOUBLE_ARROW_LEFT : '=>' ;
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
MODULE : 'module' ;
MATCH : 'match' ;
CASE : 'case' ;

// identifiers
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*  ;

STRING : '"' (~["\r\n\\] | '\\' ~[\r\n])*  '"'
       | '\'' (~["\r\n\\] | '\\' ~[\r\n])*  '\''
       ;

INT     : [0-9]+ ;
DECIMAL     : INT '.' INT ;
