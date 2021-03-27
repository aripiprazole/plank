// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import java.util.List;

import com.lorenzoog.plank.intellijplugin.psi.PlankFactor;
import com.lorenzoog.plank.intellijplugin.psi.PlankUnary;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import java.util.Map;

public class PlankFactorImpl extends ASTWrapperPsiElement implements PlankFactor {

  public PlankFactorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitFactor(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankUnary> getUnaryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankUnary.class);
  }

  @Override
  public @NotNull PlankUnary getLhs() {
    return PlankPsiImplUtil.getLhs(this);
  }

  @Override
  public @NotNull Map<String, PlankUnary> getRightmostOperands() {
    return PlankPsiImplUtil.getRightmostOperands(this);
  }

}
