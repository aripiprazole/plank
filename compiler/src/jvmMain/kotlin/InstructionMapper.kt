package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.BindingContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.EnumDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.FunDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.ImportDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.LetDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.ModuleDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.NativeFunDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.decl.StructDeclInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.AccessInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.AssignInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.BinaryInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.CallInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.ConcatInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.ConstInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.FBinaryInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GetInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GroupInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.InstanceInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.LogicalInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.MatchInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.RefInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.SetInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.SizeofInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.UnaryInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.DerefInstruction
import com.gabrielleeg1.plank.compiler.instructions.stmt.ExprStmtInstruction
import com.gabrielleeg1.plank.compiler.instructions.stmt.ReturnInstruction
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Stmt

class InstructionMapper(
  val binding: BindingContext
) : Expr.Visitor<CompilerInstruction>, Stmt.Visitor<CompilerInstruction> {
  override fun visitIfExpr(anIf: Expr.If): CompilerInstruction {
    return IfInstruction(anIf)
  }

  override fun visitConstExpr(const: Expr.Const): CompilerInstruction {
    return ConstInstruction(const)
  }

  override fun visitLogicalExpr(logical: Expr.Logical): CompilerInstruction {
    return LogicalInstruction(logical)
  }

  override fun visitBinaryExpr(binary: Expr.Binary): CompilerInstruction {
    if (binding.visit(binary).isFP) {
      return FBinaryInstruction(binary)
    }

    return BinaryInstruction(binary)
  }

  override fun visitUnaryExpr(unary: Expr.Unary): CompilerInstruction {
    return UnaryInstruction(unary)
  }

  override fun visitCallExpr(call: Expr.Call): CompilerInstruction {
    return CallInstruction(call)
  }

  override fun visitAssignExpr(assign: Expr.Assign): CompilerInstruction {
    return AssignInstruction(assign)
  }

  override fun visitSetExpr(set: Expr.Set): CompilerInstruction {
    return SetInstruction(set)
  }

  override fun visitGetExpr(get: Expr.Get): CompilerInstruction {
    return GetInstruction(get)
  }

  override fun visitGroupExpr(group: Expr.Group): CompilerInstruction {
    return GroupInstruction(group)
  }

  override fun visitExprStmt(exprStmt: Stmt.ExprStmt): CompilerInstruction {
    return ExprStmtInstruction(exprStmt)
  }

  override fun visitReturnStmt(returnStmt: Stmt.ReturnStmt): CompilerInstruction {
    return ReturnInstruction(returnStmt)
  }

  override fun visitStructDecl(structDecl: Decl.StructDecl): CompilerInstruction {
    return StructDeclInstruction(structDecl)
  }

  override fun visitFunDecl(funDecl: Decl.FunDecl): CompilerInstruction {
    if (funDecl.isNative) {
      return NativeFunDeclInstruction(funDecl)
    }

    return FunDeclInstruction(funDecl)
  }

  override fun visitLetDecl(letDecl: Decl.LetDecl): CompilerInstruction {
    return LetDeclInstruction(letDecl)
  }

  override fun visitAccessExpr(access: Expr.Access): CompilerInstruction {
    return AccessInstruction(access)
  }

  override fun visitInstanceExpr(instance: Expr.Instance): CompilerInstruction {
    return InstanceInstruction(instance)
  }

  override fun visitSizeofExpr(sizeof: Expr.Sizeof): CompilerInstruction {
    return SizeofInstruction(sizeof)
  }

  override fun visitRefExpr(reference: Expr.Reference): CompilerInstruction {
    return RefInstruction(reference)
  }

  override fun visitDerefExpr(value: Expr.Value): CompilerInstruction {
    return DerefInstruction(value)
  }

  override fun visitModuleDecl(moduleDecl: Decl.ModuleDecl): CompilerInstruction {
    return ModuleDeclInstruction(moduleDecl)
  }

  override fun visitImportDecl(importDecl: Decl.ImportDecl): CompilerInstruction {
    return ImportDeclInstruction(importDecl)
  }

  override fun visitConcatExpr(concat: Expr.Concat): CompilerInstruction {
    return ConcatInstruction()
  }

  override fun visitEnumDecl(enumDecl: Decl.EnumDecl): CompilerInstruction {
    return EnumDeclInstruction(enumDecl)
  }

  override fun visitMatchExpr(match: Expr.Match): CompilerInstruction {
    return MatchInstruction(match)
  }
}
