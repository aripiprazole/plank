package org.plank.codegen

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.element.ResolvedUseDecl
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedEnumIndexAccess
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.analyzer.element.TypedIntOperationExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.codegen.expr.AccessInst
import org.plank.codegen.expr.AssignInst
import org.plank.codegen.expr.BlockInst
import org.plank.codegen.expr.CallInst
import org.plank.codegen.expr.ConstInst
import org.plank.codegen.expr.DerefInst
import org.plank.codegen.expr.GetInst
import org.plank.codegen.expr.GroupInst
import org.plank.codegen.expr.IfInst
import org.plank.codegen.expr.InstanceInst
import org.plank.codegen.expr.IntOperationInst
import org.plank.codegen.expr.MatchInst
import org.plank.codegen.expr.RefInst
import org.plank.codegen.expr.SetInst
import org.plank.codegen.expr.SizeofInst
import org.plank.codegen.stmt.EnumInst
import org.plank.codegen.stmt.ExprInst
import org.plank.codegen.stmt.FunInst
import org.plank.codegen.stmt.ImportInst
import org.plank.codegen.stmt.LetInst
import org.plank.codegen.stmt.ModuleInst
import org.plank.codegen.stmt.ReturnInst
import org.plank.codegen.stmt.StructInst

fun exprToInstruction(expr: TypedExpr): CodegenInstruction = when (expr) {
  is TypedEnumIndexAccess -> TODO()
  is TypedAccessExpr -> AccessInst(expr)
  is TypedAssignExpr -> AssignInst(expr)
  is TypedBlockExpr -> BlockInst(expr)
  is TypedCallExpr -> CallInst(expr)
  is TypedConstExpr -> ConstInst(expr)
  is TypedDerefExpr -> DerefInst(expr)
  is TypedGetExpr -> GetInst(expr)
  is TypedGroupExpr -> GroupInst(expr)
  is TypedIfExpr -> IfInst(expr)
  is TypedInstanceExpr -> InstanceInst(expr)
  is TypedMatchExpr -> MatchInst(expr)
  is TypedRefExpr -> RefInst(expr)
  is TypedSetExpr -> SetInst(expr)
  is TypedSizeofExpr -> SizeofInst(expr)
  is TypedIntOperationExpr -> IntOperationInst(expr)
}

fun stmtToInstruction(stmt: ResolvedStmt): CodegenInstruction = when (stmt) {
  is ResolvedEnumDecl -> EnumInst(stmt)
  is ResolvedFunDecl -> FunInst(stmt)
  is ResolvedLetDecl -> LetInst(stmt)
  is ResolvedModuleDecl -> ModuleInst(stmt)
  is ResolvedStructDecl -> StructInst(stmt)
  is ResolvedUseDecl -> ImportInst(stmt)
  is ResolvedExprStmt -> ExprInst(stmt)
  is ResolvedReturnStmt -> ReturnInst(stmt)
}
