package org.plank.codegen

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.element.ResolvedErrorDecl
import org.plank.analyzer.element.ResolvedErrorStmt
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedImportDecl
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedAccessModuleExpr
import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedErrorExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.codegen.expr.AccessInst
import org.plank.codegen.expr.AccessModuleInst
import org.plank.codegen.expr.AssignInst
import org.plank.codegen.expr.CallInst
import org.plank.codegen.expr.ConstInst
import org.plank.codegen.expr.DerefInst
import org.plank.codegen.expr.GetInst
import org.plank.codegen.expr.GroupInst
import org.plank.codegen.expr.IfInst
import org.plank.codegen.expr.InstanceInst
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

interface InstructionMapper :
  TypedExpr.Visitor<CodegenInstruction>,
  ResolvedStmt.Visitor<CodegenInstruction> {
  companion object : InstructionMapper {
    override fun visitConstExpr(expr: TypedConstExpr): CodegenInstruction {
      return ConstInst(expr)
    }

    override fun visitIfExpr(expr: TypedIfExpr): CodegenInstruction {
      return IfInst(expr)
    }

    override fun visitAccessExpr(expr: TypedAccessExpr): CodegenInstruction {
      return AccessInst(expr)
    }

    override fun visitCallExpr(expr: TypedCallExpr): CodegenInstruction {
      return CallInst(expr)
    }

    override fun visitAssignExpr(expr: TypedAssignExpr): CodegenInstruction {
      return AssignInst(expr)
    }

    override fun visitSetExpr(expr: TypedSetExpr): CodegenInstruction {
      return SetInst(expr)
    }

    override fun visitAccessModuleExpr(expr: TypedAccessModuleExpr): CodegenInstruction {
      return AccessModuleInst(expr)
    }

    override fun visitGetExpr(expr: TypedGetExpr): CodegenInstruction {
      return GetInst(expr)
    }

    override fun visitGroupExpr(expr: TypedGroupExpr): CodegenInstruction {
      return GroupInst(expr)
    }

    override fun visitInstanceExpr(expr: TypedInstanceExpr): CodegenInstruction {
      return InstanceInst(expr)
    }

    override fun visitSizeofExpr(expr: TypedSizeofExpr): CodegenInstruction {
      return SizeofInst(expr)
    }

    override fun visitReferenceExpr(expr: TypedRefExpr): CodegenInstruction {
      return RefInst(expr)
    }

    override fun visitDerefExpr(expr: TypedDerefExpr): CodegenInstruction {
      return DerefInst(expr)
    }

    override fun visitMatchExpr(expr: TypedMatchExpr): CodegenInstruction {
      return MatchInst(expr)
    }

    override fun visitViolatedExpr(expr: TypedErrorExpr): CodegenInstruction {
      error("Cant generate violated expr")
    }

    override fun visitExprStmt(stmt: ResolvedExprStmt): CodegenInstruction {
      return ExprInst(stmt)
    }

    override fun visitReturnStmt(stmt: ResolvedReturnStmt): CodegenInstruction {
      return ReturnInst(stmt)
    }

    override fun visitImportDecl(decl: ResolvedImportDecl): CodegenInstruction {
      return ImportInst(decl)
    }

    override fun visitModuleDecl(decl: ResolvedModuleDecl): CodegenInstruction {
      return ModuleInst(decl)
    }

    override fun visitEnumDecl(decl: ResolvedEnumDecl): CodegenInstruction {
      return EnumInst(decl)
    }

    override fun visitStructDecl(decl: ResolvedStructDecl): CodegenInstruction {
      return StructInst(decl)
    }

    override fun visitFunDecl(decl: ResolvedFunDecl): CodegenInstruction {
      return FunInst(decl)
    }

    override fun visitLetDecl(decl: ResolvedLetDecl): CodegenInstruction {
      return LetInst(decl)
    }

    override fun visitViolatedStmt(stmt: ResolvedErrorStmt): CodegenInstruction {
      error("Cant generate violated stmt")
    }

    override fun visitViolatedDecl(stmt: ResolvedErrorDecl): CodegenInstruction {
      error("Cant generate violated decl")
    }
  }
}
