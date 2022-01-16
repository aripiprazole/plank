parser grammar PlankParser;

options {tokenVocab=PlankLexer;}

// file
file: fileModule? decl* ;

fileModule: MODULE path=qualifiedPath semis;

// utils
semis: SEMICOLON ws;

ws: (NEWLINE | WS)*;

// types
typeReference: argument=typeReference ARROW_LEFT returnType=typeReference # FunctionTypeRef
             | value=typePrimary # PrimaryTypeRef
             ;

typePrimary: path=qualifiedPath # AccessTypeRef
           | LBRACKET type=typePrimary RBRACKET # ArrayTypeRef
           | TIMES type=typePrimary # PointerTypeRef
           | LPAREN type=typeReference RPAREN # GroupTypeRef
           ;

parameter: name=IDENTIFIER COLON type=typeReference;

// types
property: MUTABLE? parameter;

// enums
enumMember: name=IDENTIFIER (LPAREN (typeReference (COMMA typeReference))? RPAREN)?;

// decls
decl: TYPE name=IDENTIFIER EQUAL (LBRACE (property (COMMA property)*)? RBRACE) semis # StructDecl
    | TYPE name=IDENTIFIER EQUAL (BAR enumMember)* semis # EnumDecl
    | MODULE path=qualifiedPath LBRACE decl* RBRACE semis # ModuleDecl
    | IMPORT path=qualifiedPath semis # ImportDecl
    | attribute* FUN name=IDENTIFIER LPAREN (parameter (COMMA parameter)*)? RPAREN functionReturn? functionBody? # FunDecl
    | LET MUTABLE? name=IDENTIFIER EQUAL value=expr semis  # InferLetDecl
    | LET MUTABLE? name=IDENTIFIER COLON type=typeReference EQUAL value=expr semis # DefinedLetDecl
    ;

qualifiedPath: IDENTIFIER (DOT IDENTIFIER)*;

functionReturn: COLON returnType=typeReference;

functionBody: LBRACE stmt* RBRACE;

// attribute
// TODO: add support for nesting attributes
attributeArgument: INT # IntAttributePrimary
                 | DECIMAL # DecimalAttributePrimary
                 | STRING # StringAttributePrimary
                 | IDENTIFIER # IdentifierAttributePrimary
                 | (TRUE | FALSE) # BooleanAttributePrimary
                 ;

attribute: AT name=IDENTIFIER (LPAREN (attributeArgument (COMMA attributeArgument)*)? RPAREN)?;

// stmts
stmt: decl # DeclStmt
    | value=expr semis # ExprStmt
    | RETURN value=expr? semis # ReturnStmt
    ;

// patterns
pattern: type=qualifiedPath LPAREN (pattern (COMMA pattern)*)? RPAREN # NamedTuplePattern
       | name=IDENTIFIER # IdentPattern;

// exprs
expr: assignExpr # AssignExprProvider
    | IF LPAREN cond=expr RPAREN thenBranch=expr elseBranch? # IfExpr
    | type=typeReference LBRACE body=instanceBody RBRACE # InstanceExpr
    | SIZEOF type=typeReference # SizeofExpr
    | MATCH subject=expr LBRACE (matchPattern (COMMA matchPattern))? RBRACE  # MatchExpr
    ;

matchPattern: pattern DOUBLE_ARROW_LEFT value=expr;

instanceBody: instanceArgument (COMMA instanceArgument)*;

instanceArgument: name=IDENTIFIER COLON value=expr;

elseBranch: ELSE value=expr;

// maybe use callExpr for delegate variables
assignExpr: name=IDENTIFIER ASSIGN value=assignExpr # AssignExprHolder
          | receiver=callExpr /* workaround */ ASSIGN value=assignExpr # SetExprHolder
          | value=logicalExpr # AssignValueHolder
          ;

logicalExpr: lhs=logicalExpr op=(EQ | NEQ) rhs=logicalExpr # LogicalExprHolder
           | lhs=logicalExpr op=(GT | GTE | LT | LTE) rhs=logicalExpr # LogicalExprHolder
           | value=binaryExpr # LogicalValueHolder
           ;

binaryExpr : lhs=binaryExpr op=(TIMES | DIV) rhs=binaryExpr # BinaryExprHolder
           | lhs=binaryExpr op=(ADD | CONCAT | SUB) rhs=binaryExpr # BinaryExprHolder
           | value=unaryExpr # BinaryValueHolder
           ;

unaryExpr : op=(BANG | SUB) rhs=unaryExpr # UnaryExprHolder
          | value=callExpr # UnaryValueHolder
          ;

argumentFragment: LPAREN (expr (COMMA expr)*)? RPAREN # CallArgument
                | DOT IDENTIFIER # GetArgument
                ;

callExpr: callee=reference argumentFragment*;

reference: AMPERSTAND value=expr # RefExpr
         | TIMES value=expr # DerefExpr
         | primary # ConstExpr
         ;

primary: INT # IntPrimary
       | DECIMAL # DecimalPrimary
       | STRING # StringPrimary
       | IDENTIFIER # IdentifierPrimary
       | (TRUE | FALSE) # BooleanPrimary
       | (LPAREN value=expr RPAREN) # GroupPrimary
       ;
