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

public class PlankAssignExprImpl extends ASTWrapperPsiElement implements PlankAssignExpr {

  public PlankAssignExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitAssignExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankAssign getAssign() {
    return findChildByClass(PlankAssign.class);
  }

  @Override
  @Nullable
  public PlankEquality getEquality() {
    return findChildByClass(PlankEquality.class);
  }

  @Override
  @Nullable
  public PlankSet getSet() {
    return findChildByClass(PlankSet.class);
  }

  @Override
  public @NotNull PsiElement getValue() {
    return PlankPsiImplUtil.getValue(this);
  }

}
