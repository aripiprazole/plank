lexer grammar PlankLexer;

WS: (' ' | '\t' | NEWLINE)+ -> channel(HIDDEN);
NEWLINE: ([\r] | [\n])+;

// symbols
AT: '@';

SEMICOLON : ';' ;
COMMA: ',';
COLON: ':';

BAR: '|';

LPAREN: '(';
RPAREN: ')';

LBRACE: '{';
RBRACE: '}';

LBRACKET: '[' ;
RBRACKET: ']' ;

APOSTROPHE: '\'';

DOT: '.';

AMPERSTAND: '&';

ADD: '+';
SUB: '-';
DIV: '/';
TIMES: '*';
CONCAT: ADD ADD;

BANG: '!';
EQUAL: '=';
ASSIGN: COLON EQUAL;

GT: '>';
LT: '<';
GTE: GT EQUAL;
LTE: LT EQUAL;
EQ: EQUAL EQUAL;
NEQ: BANG EQUAL;

DOUBLE_ARROW_LEFT: EQUAL GT;
ARROW_LEFT: SUB GT;

// keywords
RETURN: 'return';
FUN: 'fun';
TYPE: 'type';
LET: 'let';
IF: 'if';
ELSE: 'else';
MUTABLE: 'mutable';
TRUE: 'true';
FALSE: 'false';
USE: 'use';
SIZEOF: 'sizeof';
MODULE: 'module';
MATCH: 'match';
ENUM: 'enum';
THEN: 'then';

// identifiers
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

STRING: '"' (~["\r\n\\] | '\\' ~[\r\n])*  '"'
      | '\'' (~["\r\n\\] | '\\' ~[\r\n])*  '\''
      ;

INT: [0-9]+ ;
DECIMAL: INT '.' INT;
