lexer grammar PlankLexer;

WS : (' ' | '\t' | NEWLINE)+ -> channel(HIDDEN);
NEWLINE : ([\r\n] | [\n])+;

// symbols
AT : '@' ;

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
CONCAT: ADD ADD;

AMPERSTAND: '&';

BANG: '!';
ASSIGN: COLON EQUAL;
EQUAL: '=';

EQ: EQUAL EQUAL;
NEQ: BANG EQUAL;
GTE: GT EQUAL;
GT: '>';
LT: '<';
LTE: LT EQUAL;

APOSTROPHE: '\'';

DOUBLE_ARROW_LEFT : EQUAL GT ;
ARROW_LEFT : SUB GT ;

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
