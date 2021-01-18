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

public class PlankExprImpl extends ASTWrapperPsiElement implements PlankExpr {

  public PlankExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankAssignExpr getAssignExpr() {
    return findChildByClass(PlankAssignExpr.class);
  }

  @Override
  @Nullable
  public PlankIfExpr getIfExpr() {
    return findChildByClass(PlankIfExpr.class);
  }

  @Override
  @Nullable
  public PlankInstanceExpr getInstanceExpr() {
    return findChildByClass(PlankInstanceExpr.class);
  }

  @Override
  @Nullable
  public PlankSizeofExpr getSizeofExpr() {
    return findChildByClass(PlankSizeofExpr.class);
  }

}
