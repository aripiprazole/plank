// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.PlankCallExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankUnary;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankUnaryImpl extends ASTWrapperPsiElement implements PlankUnary {

  public PlankUnaryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitUnary(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankCallExpr getCallExpr() {
    return findChildByClass(PlankCallExpr.class);
  }

  @Override
  @Nullable
  public PlankUnary getUnary() {
    return findChildByClass(PlankUnary.class);
  }

  @Override
  public @Nullable String getOperator() {
    return PlankPsiImplUtil.getOperator(this);
  }

}
