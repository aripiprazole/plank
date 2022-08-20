parser grammar PlankParser;

options {tokenVocab=PlankLexer;}

// file
file: module? decl* ;

module: MODULE path=qualifiedPath semis;

// utils
semis: SEMICOLON ws;

optionalSemis: semis?;

ws: (SEMICOLON | WS)*;

qualifiedPath: IDENTIFIER (DOT IDENTIFIER)*;

// types
typeRef: parameter=typeRef ARROW_LEFT returnType=typeRef # FunctionTypeRef
       | value=typePrimary                               # PrimaryTypeRef
       ;

typePrimary: path=qualifiedPath                                               # AccessTypeRef
           | path=qualifiedPath LBRACKET (typeRef (COMMA typeRef)*)? RBRACKET # ApplyTypeRef
           | TIMES    type=typePrimary                                        # PointerTypeRef
           | LPAREN   type=typeRef RPAREN                                     # GroupTypeRef
           | LPAREN RPAREN                                                    # UnitTypeRef
           ;

param: name=IDENTIFIER COLON type=typeRef;

generics: LBRACKET IDENTIFIER (COMMA IDENTIFIER)* RBRACKET;

// decls
decl: TYPE name=IDENTIFIER names=generics? EQUAL (LBRACE (prop (COMMA prop)*)? RBRACE)             optionalSemis         # StructDecl
    | ENUM name=IDENTIFIER names=generics?       (LBRACE (enumMember (COMMA enumMember))? RBRACE)? optionalSemis         # EnumDecl
    | MODULE path=qualifiedPath LBRACE decl* RBRACE                                                optionalSemis         # ModuleDecl
    | USE path=qualifiedPath                                                                       semis                 # UseDecl
    | attr* FUN name=IDENTIFIER LPAREN (param (COMMA param)*)? RPAREN (ARROW_LEFT returnType=typeRef)? body=functionBody # FunDecl
    | LET MUTABLE? name=IDENTIFIER EQUAL value=expr                                                semis                 # InferLetDecl
    | LET MUTABLE? name=IDENTIFIER COLON type=typeRef EQUAL value=expr                             semis                 # LetDecl
    ;

functionBody : LBRACE stmt* value=expr? RBRACE optionalSemis       # CodeBody
             | semis                                               # NoBody
             | EQUAL value=expr                      optionalSemis # ExprBody
             ;

// types
enumMember: name=IDENTIFIER (LPAREN typeRef (COMMA typeRef)* RPAREN)?;

prop: MUTABLE? name=IDENTIFIER COLON type=typeRef;

// attribute
// TODO: add support for nesting attributes
attr: AT name=IDENTIFIER (LPAREN (attrExpr (COMMA attrExpr)*)? RPAREN)? ;

attrExpr: value=INT        # AttrIntExpr
        | value=DECIMAL    # AttrDecimalExpr
        | value=STRING     # AttrStringExpr
        | value=IDENTIFIER # AttrAccessExpr
        | value=TRUE       # AttrTrueExpr
        | value=FALSE      # AttrFalseExpr
        ;

// stmts
stmt: value=decl               # DeclStmt
    | value=expr         semis # ExprStmt
    | RETURN value=expr? semis # ReturnStmt
    ;

// patterns
pattern: type=qualifiedPath LPAREN (pattern (COMMA pattern)*)? RPAREN # NamedTuplePattern
       | name=IDENTIFIER                                              # IdentPattern
       ;

thenBranch : THEN value=expr                 # MainThenBranch
           | LBRACE stmt* value=expr? RBRACE # BlockThenBranch
           ;

elseBranch : value=expr                      # MainElseBranch
           | LBRACE stmt* value=expr? RBRACE # BlockElseBranch
           ;

// exprs
expr: <assoc=right> name=IDENTIFIER ASSIGN       value=expr                  # AssignExpr
    | <assoc=right> receiver=primary arg* ASSIGN value=expr                  # SetExpr
    | lhs=expr op=(EQ | NEQ)            rhs=expr                             # BinaryExpr
    | lhs=expr op=(GT | GTE | LT | LTE) rhs=expr                             # BinaryExpr
    | lhs=expr op=(TIMES | DIV)         rhs=expr                             # BinaryExpr
    | lhs=expr op=(ADD | CONCAT | SUB)  rhs=expr                             # BinaryExpr
    | op=(BANG | SUB)                   rhs=expr                             # UnaryExpr
    | LBRACE stmt* value=expr? RBRACE                                        # BlockExpr
    | callee=primary arg*                                                    # CallExpr
    | type=typeRef LBRACE instanceArg (COMMA instanceArg)* RBRACE            # InstanceExpr
    | IF cond=expr mainBranch=thenBranch (ELSE otherwiseBranch=elseBranch)?  # IfExpr
    | SIZEOF type=typeRef                                                    # SizeofExpr
    | MATCH subject=expr LBRACE (matchPattern (COMMA matchPattern))? RBRACE  # MatchExpr
    ;

instanceArg: name=IDENTIFIER COLON value=expr;

arg: LPAREN (expr (COMMA expr)*)? RPAREN # CallArg
   | DOT name=IDENTIFIER                 # GetArg
   ;

matchPattern: key=pattern DOUBLE_ARROW_LEFT value=expr;

primary: AMPERSTAND value=expr         # RefExpr
       | TIMES      value=expr         # DerefExpr
       |            value=INT          # IntExpr
       |            value=DECIMAL      # DecimalExpr
       |            value=STRING       # StringExpr
       |            value=IDENTIFIER   # AccessExpr
       |            value=TRUE         # TrueExpr
       |            value=FALSE        # FalseExpr
       | LPAREN     value=expr? RPAREN # GroupExpr
       ;
