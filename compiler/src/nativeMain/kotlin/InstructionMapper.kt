package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedErrorDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedErrorStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedExprStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedImportDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedLetDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedStmt
import com.gabrielleeg1.plank.analyzer.element.ResolvedStructDecl
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedAssignExpr
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.analyzer.element.TypedConstExpr
import com.gabrielleeg1.plank.analyzer.element.TypedDerefExpr
import com.gabrielleeg1.plank.analyzer.element.TypedErrorExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.analyzer.element.TypedGetExpr
import com.gabrielleeg1.plank.analyzer.element.TypedGroupExpr
import com.gabrielleeg1.plank.analyzer.element.TypedIfExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.analyzer.element.TypedMatchExpr
import com.gabrielleeg1.plank.analyzer.element.TypedRefExpr
import com.gabrielleeg1.plank.analyzer.element.TypedSetExpr
import com.gabrielleeg1.plank.analyzer.element.TypedSizeofExpr
import com.gabrielleeg1.plank.compiler.expr.AccessInst
import com.gabrielleeg1.plank.compiler.expr.AssignInst
import com.gabrielleeg1.plank.compiler.expr.CallInst
import com.gabrielleeg1.plank.compiler.expr.ConstInst
import com.gabrielleeg1.plank.compiler.expr.DerefInst
import com.gabrielleeg1.plank.compiler.expr.GetInst
import com.gabrielleeg1.plank.compiler.expr.GroupInst
import com.gabrielleeg1.plank.compiler.expr.IfInst
import com.gabrielleeg1.plank.compiler.expr.InstanceInst
import com.gabrielleeg1.plank.compiler.expr.RefInst
import com.gabrielleeg1.plank.compiler.expr.SetInst
import com.gabrielleeg1.plank.compiler.expr.SizeofInst
import com.gabrielleeg1.plank.compiler.stmt.ExprInst
import com.gabrielleeg1.plank.compiler.stmt.FunInst
import com.gabrielleeg1.plank.compiler.stmt.ImportInst
import com.gabrielleeg1.plank.compiler.stmt.LetInst
import com.gabrielleeg1.plank.compiler.stmt.ModuleInst
import com.gabrielleeg1.plank.compiler.stmt.ReturnInst
import com.gabrielleeg1.plank.compiler.stmt.StructInst

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
      TODO("Not yet implemented")
    }

    override fun visitViolatedExpr(expr: TypedErrorExpr): CodegenInstruction {
      TODO("Not yet implemented")
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
      TODO("Not yet implemented")
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
      TODO("Not yet implemented")
    }

    override fun visitViolatedDecl(stmt: ResolvedErrorDecl): CodegenInstruction {
      TODO("Not yet implemented")
    }
  }
}
