parser grammar PlankParser;

options {tokenVocab=PlankLexer;}

// file
fileModule : MODULE qualifiedPath SEMICOLON ;

program : fileModule? decl* EOF;

// types
funType : LPAREN RPAREN ( typeDef ( COMMA typeDef ) * ) ? ARROW_LEFT returnType=typeDef ;

arrayType : LBRACKET type=typeDef RBRACKET ;

nameType : name=IDENTIFIER ;

ptrType : STAR type=typeDef ;

genericAccess : APOSTROPHE name=IDENTIFIER ;

genericUse : name=IDENTIFIER LESS typeDef* GREATER ;

typeDef : nameType | funType | arrayType | ptrType | genericAccess | genericUse ;

parameter : name=IDENTIFIER COLON type=typeDef ;

// modules
moduleDecl : MODULE name=IDENTIFIER LBRACE decl* RBRACE;

// structs
structField : MUTABLE? parameter;

structDecl : LBRACE ( structField ( COMMA structField ) * ) ? RBRACE ;

// enums
enumMember : name=IDENTIFIER ( LPAREN ( typeDef ( COMMA typeDef ) ) ? RPAREN )?;

enumDecl : ( BAR enumMember )* ;

// types
typeDecl : TYPE name=IDENTIFIER EQUAL ( structDecl | enumDecl ) SEMICOLON ;

// decls
decl : letDecl WS*
     | typeDecl WS*
     | funDecl WS*
     | moduleDecl WS*
     | importDecl WS*
     ;

letDecl : LET MUTABLE? name=IDENTIFIER EQUAL value=expr SEMICOLON
        | LET MUTABLE? name=IDENTIFIER COLON type=typeDef EQUAL value=expr SEMICOLON
        ;

qualifiedPath : IDENTIFIER ( DOT IDENTIFIER ) * ;

importDecl : IMPORT name=qualifiedPath SEMICOLON ;

funHeader : FUN name=IDENTIFIER LPAREN ( parameter ( COMMA parameter ) * ) ? RPAREN COLON returnType=typeDef ;

funDecl : funHeader LBRACE stmt* RBRACE
        | nativeFunDecl
        ;

nativeFunDecl : NATIVE funHeader SEMICOLON ;

// stmts
stmt : decl
     | ifExpr WS*
     | exprStmt WS*
     | returnStmt WS*
     ;

exprStmt : value=expr SEMICOLON ;

returnStmt : RETURN value=expr? SEMICOLON ;

// patterns
pattern : namedTuplePattern | identifierPattern ; // todo add more

namedTuplePattern : type=qualifiedPath LPAREN ( pattern ( COMMA pattern )* )? RPAREN;
identifierPattern : IDENTIFIER ;

// exprs
expr : assignExpr
     | ifExpr
     | instanceExpr
     | sizeofExpr
     | matchExpr
     ;

matchPattern : pattern DOUBLE_ARROW_LEFT expr ;

matchExpr : MATCH subject=expr LBRACE ( matchPattern ( COMMA matchPattern )* )? RBRACE ;

sizeofExpr : SIZEOF type=IDENTIFIER;

instanceArgument : IDENTIFIER COLON expr ;

instanceExpr : name=IDENTIFIER LBRACE ( instanceArgument ( COMMA instanceArgument ) )* RBRACE
             | name=IDENTIFIER LBRACE instanceArgument RBRACE
             | name=IDENTIFIER LBRACE RBRACE
             ;

elseBranch : ELSE expr
           | ELSE LBRACE stmt* RBRACE
           | ELSE LBRACE stmt RBRACE
           ;

thenBranch : LBRACE stmt* RBRACE
           | LBRACE stmt RBRACE
           | expr
           ;

ifExpr : IF LPAREN cond=expr RPAREN thenBranch elseBranch? ;

assignExpr : (callExpr DOT) ? name=IDENTIFIER EQUAL value=assignExpr
           | logicalExpr
           ;

logicalExpr : lhs=logicalExpr op=(EQUAL_EQUAL | BANG_EQUAL) rhs=logicalExpr
            | lhs=logicalExpr op=(GREATER | GREATER_EQUAL | LESS | LESS_EQUAL) rhs=logicalExpr
            | binaryExpr
            ;

binaryExpr : lhs=binaryExpr op=(STAR | SLASH) rhs=binaryExpr
           | lhs=binaryExpr op=(PLUS | PLUS_PLUS | MINUS) rhs=binaryExpr
           | unaryExpr
           ;

unaryExpr : op=(BANG | MINUS) rhs=unaryExpr
          | callExpr
          ;

get : DOT IDENTIFIER ;

arguments :  LPAREN ( expr (COMMA expr)* )? RPAREN ;

callExpr : access=ptr ( arguments | get )* ;

groupExpr : LPAREN value=expr RPAREN ;

booleanExpr : TRUE
            | FALSE
            ;

stringExpr : STRING ;

ptr : AMPERSTAND expr
    | STAR expr
    | primary
    ;

primary : INT
        | DECIMAL
        | IDENTIFIER
        | stringExpr
        | booleanExpr
        | groupExpr
        ;
