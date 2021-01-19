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

public class PlankIfExprImpl extends ASTWrapperPsiElement implements PlankIfExpr {

  public PlankIfExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitIfExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankElseBranch getElseBranch() {
    return findChildByClass(PlankElseBranch.class);
  }

  @Override
  @NotNull
  public PlankExpr getExpr() {
    return findNotNullChildByClass(PlankExpr.class);
  }

  @Override
  @NotNull
  public PlankThenBranch getThenBranch() {
    return findNotNullChildByClass(PlankThenBranch.class);
  }

}
