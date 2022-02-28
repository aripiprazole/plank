package org.plank.syntax.element

fun <A : PlankElement> walkTree(
  value: A,
  enterExpr: (Expr) -> Any = {},
  exitExpr: (Expr) -> Any = {},
  enterStmt: (Stmt) -> Any = {},
  exitStmt: (Stmt) -> Any = {},
  enterBranch: (IfBranch) -> Any = {},
  exitBranch: (IfBranch) -> Any = {},
  enterBody: (FunctionBody) -> Any = {},
  exitBody: (FunctionBody) -> Any = {},
  enterPattern: (Pattern) -> Any = {},
  exitPattern: (Pattern) -> Any = {},
  enterTypeRef: (TypeRef) -> Any = {},
  exitTypeRef: (TypeRef) -> Any = {},
) {
  fun <A> idUnit(fn: (A) -> Any): (A) -> A = { fn(it); it }

  transformTree(
    value,
    enterExpr = idUnit(enterExpr),
    exitExpr = idUnit(exitExpr),
    enterStmt = idUnit(enterStmt),
    exitStmt = idUnit(exitStmt),
    enterBranch = idUnit(enterBranch),
    exitBranch = idUnit(exitBranch),
    enterBody = idUnit(enterBody),
    exitBody = idUnit(exitBody),
    enterPattern = idUnit(enterPattern),
    exitPattern = idUnit(exitPattern),
    enterTypeRef = idUnit(enterTypeRef),
    exitTypeRef = idUnit(exitTypeRef),
  )
}
