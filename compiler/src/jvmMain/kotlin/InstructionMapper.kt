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
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.EnumInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.FunctionInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.ImportInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.LetInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.ModuleInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.NativeFunctionInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.StructInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.AccessInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.AssignInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.CallInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.ConstInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.DerefInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GetInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GroupInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.InstanceInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.MatchInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.RefInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.SetInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.SizeofInstruction
import com.gabrielleeg1.plank.compiler.instructions.stmt.ExprStmtInstruction
import com.gabrielleeg1.plank.compiler.instructions.stmt.ReturnInstruction

interface InstructionMapper :
  TypedExpr.Visitor<CompilerInstruction>,
  ResolvedStmt.Visitor<CompilerInstruction> {
  companion object : InstructionMapper {
    override fun visitExprStmt(stmt: ResolvedExprStmt): CompilerInstruction {
      return ExprStmtInstruction(stmt)
    }

    override fun visitReturnStmt(stmt: ResolvedReturnStmt): CompilerInstruction {
      return ReturnInstruction(stmt)
    }

    override fun visitImportDecl(decl: ResolvedImportDecl): CompilerInstruction {
      return ImportInstruction(decl)
    }

    override fun visitModuleDecl(decl: ResolvedModuleDecl): CompilerInstruction {
      return ModuleInstruction(decl)
    }

    override fun visitEnumDecl(decl: ResolvedEnumDecl): CompilerInstruction {
      return EnumInstruction(decl)
    }

    override fun visitStructDecl(decl: ResolvedStructDecl): CompilerInstruction {
      return StructInstruction(decl)
    }

    override fun visitFunDecl(decl: ResolvedFunDecl): CompilerInstruction {
      return if (decl.hasAttribute("external")) {
        NativeFunctionInstruction(decl)
      } else {
        FunctionInstruction(decl)
      }
    }

    override fun visitLetDecl(decl: ResolvedLetDecl): CompilerInstruction {
      return LetInstruction(decl)
    }

    override fun visitConstExpr(expr: TypedConstExpr): CompilerInstruction {
      return ConstInstruction(expr)
    }

    override fun visitIfExpr(expr: TypedIfExpr): CompilerInstruction {
      return IfInstruction(expr)
    }

    override fun visitAccessExpr(expr: TypedAccessExpr): CompilerInstruction {
      return AccessInstruction(expr)
    }

    override fun visitCallExpr(expr: TypedCallExpr): CompilerInstruction {
      return CallInstruction(expr)
    }

    override fun visitAssignExpr(expr: TypedAssignExpr): CompilerInstruction {
      return AssignInstruction(expr)
    }

    override fun visitSetExpr(expr: TypedSetExpr): CompilerInstruction {
      return SetInstruction(expr)
    }

    override fun visitGetExpr(expr: TypedGetExpr): CompilerInstruction {
      return GetInstruction(expr)
    }

    override fun visitGroupExpr(expr: TypedGroupExpr): CompilerInstruction {
      return GroupInstruction(expr)
    }

    override fun visitInstanceExpr(expr: TypedInstanceExpr): CompilerInstruction {
      return InstanceInstruction(expr)
    }

    override fun visitSizeofExpr(expr: TypedSizeofExpr): CompilerInstruction {
      return SizeofInstruction(expr)
    }

    override fun visitReferenceExpr(expr: TypedRefExpr): CompilerInstruction {
      return RefInstruction(expr)
    }

    override fun visitDerefExpr(expr: TypedDerefExpr): CompilerInstruction {
      return DerefInstruction(expr)
    }

    override fun visitMatchExpr(expr: TypedMatchExpr): CompilerInstruction {
      return MatchInstruction(expr)
    }

    override fun visitViolatedExpr(expr: TypedErrorExpr): CompilerInstruction {
      TODO("Not yet implemented")
    }

    override fun visitViolatedStmt(stmt: ResolvedErrorStmt): CompilerInstruction {
      TODO("Not yet implemented")
    }

    override fun visitViolatedDecl(stmt: ResolvedErrorDecl): CompilerInstruction {
      TODO("Not yet implemented")
    }
  }
}

