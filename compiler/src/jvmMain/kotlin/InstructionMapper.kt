package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.instructions.decl.ClassDeclInstruction
import com.lorenzoog.jplank.compiler.instructions.decl.FunDeclInstruction
import com.lorenzoog.jplank.compiler.instructions.decl.LetDeclInstruction
import com.lorenzoog.jplank.compiler.instructions.decl.NativeFunDeclInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.AccessInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.AssignInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.BinaryInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.CallInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.ConstInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.FBinaryInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.GetInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.GroupInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.IfInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.InstanceInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.LogicalInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.ReferenceInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.SetInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.SizeofInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.UnaryInstruction
import com.lorenzoog.jplank.compiler.instructions.expr.ValueInstruction
import com.lorenzoog.jplank.compiler.instructions.stmt.ExprStmtInstruction
import com.lorenzoog.jplank.compiler.instructions.stmt.ReturnInstruction
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Stmt

class InstructionMapper(
  val typeMapper: TypeMapper,
  val binding: BindingContext
) :
  Expr.Visitor<PlankInstruction>,
  Stmt.Visitor<PlankInstruction> {
  override fun visitIfExpr(anIf: Expr.If): PlankInstruction {
    return IfInstruction(anIf)
  }

  override fun visitConstExpr(const: Expr.Const): PlankInstruction {
    return ConstInstruction(const)
  }

  override fun visitLogicalExpr(logical: Expr.Logical): PlankInstruction {
    return LogicalInstruction(logical)
  }

  override fun visitBinaryExpr(binary: Expr.Binary): PlankInstruction {
    if (binding.visit(binary).isFP) {
      return FBinaryInstruction(binary)
    }

    return BinaryInstruction(binary)
  }

  override fun visitUnaryExpr(unary: Expr.Unary): PlankInstruction {
    return UnaryInstruction(unary)
  }

  override fun visitCallExpr(call: Expr.Call): PlankInstruction {
    return CallInstruction(call)
  }

  override fun visitAssignExpr(assign: Expr.Assign): PlankInstruction {
    return AssignInstruction(assign)
  }

  override fun visitSetExpr(set: Expr.Set): PlankInstruction {
    return SetInstruction(set)
  }

  override fun visitGetExpr(get: Expr.Get): PlankInstruction {
    return GetInstruction(get)
  }

  override fun visitGroupExpr(group: Expr.Group): PlankInstruction {
    return GroupInstruction(group)
  }

  override fun visitExprStmt(exprStmt: Stmt.ExprStmt): PlankInstruction {
    return ExprStmtInstruction(exprStmt)
  }

  override fun visitReturnStmt(returnStmt: Stmt.ReturnStmt): PlankInstruction {
    return ReturnInstruction(returnStmt)
  }

  override fun visitClassDecl(structDecl: Decl.StructDecl): PlankInstruction {
    return ClassDeclInstruction(structDecl)
  }

  override fun visitFunDecl(funDecl: Decl.FunDecl): PlankInstruction {
    if (funDecl.isNative) {
      return NativeFunDeclInstruction(funDecl)
    }

    return FunDeclInstruction(funDecl)
  }

  override fun visitLetDecl(letDecl: Decl.LetDecl): PlankInstruction {
    return LetDeclInstruction(letDecl)
  }

  override fun visitAccessExpr(access: Expr.Access): PlankInstruction {
    return AccessInstruction(access)
  }

  override fun visitInstanceExpr(instance: Expr.Instance): PlankInstruction {
    return InstanceInstruction(instance)
  }

  override fun visitSizeofExpr(sizeof: Expr.Sizeof): PlankInstruction {
    return SizeofInstruction(sizeof)
  }

  override fun visitReferenceExpr(reference: Expr.Reference): PlankInstruction {
    return ReferenceInstruction(reference)
  }

  override fun visitValueExpr(value: Expr.Value): PlankInstruction {
    return ValueInstruction(value)
  }

  override fun visitModuleDecl(moduleDecl: Decl.ModuleDecl): PlankInstruction {
    TODO("Not yet implemented")
  }

  override fun visitImportDecl(importDecl: Decl.ImportDecl): PlankInstruction {
    TODO("Not yet implemented")
  }
}
