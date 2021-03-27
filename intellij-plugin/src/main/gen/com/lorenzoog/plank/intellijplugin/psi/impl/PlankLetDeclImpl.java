// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.PlankExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankLetDecl;
import com.lorenzoog.plank.intellijplugin.psi.PlankTypeDef;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankLetDeclImpl extends ASTWrapperPsiElement implements PlankLetDecl {

  public PlankLetDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitLetDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PlankExpr getExpr() {
    return findNotNullChildByClass(PlankExpr.class);
  }

  @Override
  @Nullable
  public PlankTypeDef getTypeDef() {
    return findChildByClass(PlankTypeDef.class);
  }

  @Override
  public @NotNull String getName() {
    return PlankPsiImplUtil.getName(this);
  }

  @Override
  public boolean getMutable() {
    return PlankPsiImplUtil.getMutable(this);
  }

}
