package com.lorenzoog.plank.compiler

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.decl.EnumDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.FunDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.ImportDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.LetDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.ModuleDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.NativeFunDeclInstruction
import com.lorenzoog.plank.compiler.instructions.decl.StructDeclInstruction
import com.lorenzoog.plank.compiler.instructions.expr.AccessInstruction
import com.lorenzoog.plank.compiler.instructions.expr.AssignInstruction
import com.lorenzoog.plank.compiler.instructions.expr.BinaryInstruction
import com.lorenzoog.plank.compiler.instructions.expr.CallInstruction
import com.lorenzoog.plank.compiler.instructions.expr.ConcatInstruction
import com.lorenzoog.plank.compiler.instructions.expr.ConstInstruction
import com.lorenzoog.plank.compiler.instructions.expr.FBinaryInstruction
import com.lorenzoog.plank.compiler.instructions.expr.GetInstruction
import com.lorenzoog.plank.compiler.instructions.expr.GroupInstruction
import com.lorenzoog.plank.compiler.instructions.expr.IfInstruction
import com.lorenzoog.plank.compiler.instructions.expr.InstanceInstruction
import com.lorenzoog.plank.compiler.instructions.expr.LogicalInstruction
import com.lorenzoog.plank.compiler.instructions.expr.ReferenceInstruction
import com.lorenzoog.plank.compiler.instructions.expr.SetInstruction
import com.lorenzoog.plank.compiler.instructions.expr.SizeofInstruction
import com.lorenzoog.plank.compiler.instructions.expr.UnaryInstruction
import com.lorenzoog.plank.compiler.instructions.expr.ValueInstruction
import com.lorenzoog.plank.compiler.instructions.stmt.ExprStmtInstruction
import com.lorenzoog.plank.compiler.instructions.stmt.ReturnInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Stmt

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

  override fun visitReferenceExpr(reference: Expr.Reference): CompilerInstruction {
    return ReferenceInstruction(reference)
  }

  override fun visitValueExpr(value: Expr.Value): CompilerInstruction {
    return ValueInstruction(value)
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
    TODO("Not yet implemented")
  }
}
