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
import java.util.Map;

public class PlankEqualityImpl extends ASTWrapperPsiElement implements PlankEquality {

  public PlankEqualityImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitEquality(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankComparison> getComparisonList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankComparison.class);
  }

  @Override
  public @NotNull PlankComparison getLhs() {
    return PlankPsiImplUtil.getLhs(this);
  }

  @Override
  public @NotNull Map<String, PlankComparison> getRightmostOperands() {
    return PlankPsiImplUtil.getRightmostOperands(this);
  }

}
