// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import com.lorenzoog.jplank.intellijplugin.psi.PlankNamedElement;
import com.lorenzoog.jplank.intellijplugin.psi.PsiScope;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class PlankVisitor extends PsiElementVisitor {

  public void visitArgument(@NotNull PlankArgument o) {
    visitPsiElement(o);
  }

  public void visitArguments(@NotNull PlankArguments o) {
    visitPsiElement(o);
  }

  public void visitArrayTypeDef(@NotNull PlankArrayTypeDef o) {
    visitPsiElement(o);
  }

  public void visitAssign(@NotNull PlankAssign o) {
    visitPsiElement(o);
  }

  public void visitAssignExpr(@NotNull PlankAssignExpr o) {
    visitPsiElement(o);
  }

  public void visitCallExpr(@NotNull PlankCallExpr o) {
    visitPsiElement(o);
  }

  public void visitCallableTypeDef(@NotNull PlankCallableTypeDef o) {
    visitPsiElement(o);
  }

  public void visitClassDecl(@NotNull PlankClassDecl o) {
    visitNamedElement(o);
  }

  public void visitComparison(@NotNull PlankComparison o) {
    visitPsiElement(o);
  }

  public void visitDecl(@NotNull PlankDecl o) {
    visitPsiElement(o);
  }

  public void visitElseBranch(@NotNull PlankElseBranch o) {
    visitPsiScope(o);
  }

  public void visitEquality(@NotNull PlankEquality o) {
    visitPsiElement(o);
  }

  public void visitExpr(@NotNull PlankExpr o) {
    visitPsiElement(o);
  }

  public void visitExprStmt(@NotNull PlankExprStmt o) {
    visitPsiElement(o);
  }

  public void visitFactor(@NotNull PlankFactor o) {
    visitPsiElement(o);
  }

  public void visitField(@NotNull PlankField o) {
    visitPsiElement(o);
  }

  public void visitFunDecl(@NotNull PlankFunDecl o) {
    visitPsiScope(o);
  }

  public void visitFunHeader(@NotNull PlankFunHeader o) {
    visitNamedElement(o);
  }

  public void visitGenericAccessTypeDef(@NotNull PlankGenericAccessTypeDef o) {
    visitPsiElement(o);
  }

  public void visitGenericTypeDef(@NotNull PlankGenericTypeDef o) {
    visitPsiElement(o);
  }

  public void visitGet(@NotNull PlankGet o) {
    visitNamedElement(o);
  }

  public void visitIdentifierExpr(@NotNull PlankIdentifierExpr o) {
    visitNamedElement(o);
  }

  public void visitIfExpr(@NotNull PlankIfExpr o) {
    visitPsiElement(o);
  }

  public void visitImportDirective(@NotNull PlankImportDirective o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull PlankImports o) {
    visitPsiElement(o);
  }

  public void visitInstanceArguments(@NotNull PlankInstanceArguments o) {
    visitPsiElement(o);
  }

  public void visitLetDecl(@NotNull PlankLetDecl o) {
    visitPsiElement(o);
  }

  public void visitNameTypeDef(@NotNull PlankNameTypeDef o) {
    visitNamedElement(o);
  }

  public void visitNativeFunDecl(@NotNull PlankNativeFunDecl o) {
    visitPsiElement(o);
  }

  public void visitParameter(@NotNull PlankParameter o) {
    visitNamedElement(o);
  }

  public void visitPointer(@NotNull PlankPointer o) {
    visitPsiElement(o);
  }

  public void visitPointerTypeDef(@NotNull PlankPointerTypeDef o) {
    visitPsiElement(o);
  }

  public void visitPrimary(@NotNull PlankPrimary o) {
    visitPsiElement(o);
  }

  public void visitReturnStmt(@NotNull PlankReturnStmt o) {
    visitPsiElement(o);
  }

  public void visitSet(@NotNull PlankSet o) {
    visitPsiElement(o);
  }

  public void visitSizeofExpr(@NotNull PlankSizeofExpr o) {
    visitPsiElement(o);
  }

  public void visitStmt(@NotNull PlankStmt o) {
    visitPsiElement(o);
  }

  public void visitTerm(@NotNull PlankTerm o) {
    visitPsiElement(o);
  }

  public void visitThenBranch(@NotNull PlankThenBranch o) {
    visitPsiScope(o);
  }

  public void visitTypeDef(@NotNull PlankTypeDef o) {
    visitPsiElement(o);
  }

  public void visitUnary(@NotNull PlankUnary o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull PlankNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiScope(@NotNull PsiScope o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
