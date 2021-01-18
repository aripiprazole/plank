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

public class PlankCallExprImpl extends ASTWrapperPsiElement implements PlankCallExpr {

  public PlankCallExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitCallExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankArguments> getArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankArguments.class);
  }

  @Override
  @NotNull
  public List<PlankGet> getGetList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankGet.class);
  }

  @Override
  @NotNull
  public PlankPointer getPointer() {
    return findNotNullChildByClass(PlankPointer.class);
  }

}
