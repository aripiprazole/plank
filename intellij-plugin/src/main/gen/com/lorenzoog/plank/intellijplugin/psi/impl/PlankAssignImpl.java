// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.PlankAssign;
import com.lorenzoog.plank.intellijplugin.psi.PlankAssignExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankAssignImpl extends ASTWrapperPsiElement implements PlankAssign {

  public PlankAssignImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitAssign(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PlankAssignExpr getAssignExpr() {
    return findNotNullChildByClass(PlankAssignExpr.class);
  }

  @Override
  public @NotNull PlankNamedElement getReceiver() {
    return PlankPsiImplUtil.getReceiver(this);
  }

  @Override
  public @NotNull PsiElement getValue() {
    return PlankPsiImplUtil.getValue(this);
  }

}
