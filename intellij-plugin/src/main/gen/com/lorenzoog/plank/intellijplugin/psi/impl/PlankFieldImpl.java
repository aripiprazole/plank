// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.PlankField;
import com.lorenzoog.plank.intellijplugin.psi.PlankParameter;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankFieldImpl extends ASTWrapperPsiElement implements PlankField {

  public PlankFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitField(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PlankParameter getParameter() {
    return findNotNullChildByClass(PlankParameter.class);
  }

  @Override
  public boolean getMutable() {
    return PlankPsiImplUtil.getMutable(this);
  }

}
