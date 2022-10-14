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
CONCAT: '++';

BANG: '!';
EQUAL: '=';
ASSIGN: ':=';

GT: '>';
LT: '<';
GTE: '>=';
LTE: '<=';
EQ: '==';
NEQ: '!=';

DOUBLE_ARROW_LEFT: '=>';
ARROW_LEFT: '->';

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

/// This is not `QUALIFIED_PATH` because when creating a parser rule for this
/// the parser can not distinguish between a `QUALIFIED_PATH` and a `qualifiedPath`,
/// and it is thrown the following error with an input of `Main`:
/// "line 1:0 mismatched input 'Main' expecting IDENTIFIER"
PATH: IDENTIFIER ('.' IDENTIFIER)*;

STRING: '"' (~["\r\n\\] | '\\' ~[\r\n])*  '"';

INT: [0-9]+ ;
DECIMAL: INT '.' INT;

// semis
SEMIS: SEMICOLON WS;

OPTIONAL_SEMIS: (SEMIS) -> skip;
