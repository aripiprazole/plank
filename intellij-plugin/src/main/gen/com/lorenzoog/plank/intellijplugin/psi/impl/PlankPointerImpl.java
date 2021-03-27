// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.PlankExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankPointer;
import com.lorenzoog.plank.intellijplugin.psi.PlankPrimary;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankPointerImpl extends ASTWrapperPsiElement implements PlankPointer {

  public PlankPointerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitPointer(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankExpr getExpr() {
    return findChildByClass(PlankExpr.class);
  }

  @Override
  @Nullable
  public PlankPrimary getPrimary() {
    return findChildByClass(PlankPrimary.class);
  }

}
