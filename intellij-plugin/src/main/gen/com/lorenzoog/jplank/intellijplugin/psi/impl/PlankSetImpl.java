// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lorenzoog.jplank.intellijplugin.psi.PlankTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankSetImpl extends ASTWrapperPsiElement implements PlankSet {

  public PlankSetImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitSet(this);
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
  @Nullable
  public PlankCallExpr getCallExpr() {
    return findChildByClass(PlankCallExpr.class);
  }

  @Override
  public @NotNull PlankDotQualifiedExpr getReceiver() {
    return PlankPsiImplUtil.getReceiver(this);
  }

  @Override
  public @NotNull PsiElement getValue() {
    return PlankPsiImplUtil.getValue(this);
  }

}
