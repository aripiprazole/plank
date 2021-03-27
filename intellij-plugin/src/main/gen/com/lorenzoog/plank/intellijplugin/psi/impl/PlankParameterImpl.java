// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.jplank.intellijplugin.psi.impl.PlankNamedElementImpl;
import com.lorenzoog.plank.intellijplugin.psi.PlankParameter;
import com.lorenzoog.plank.intellijplugin.psi.PlankTypeDef;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankParameterImpl extends PlankNamedElementImpl implements PlankParameter {

  public PlankParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PlankTypeDef getTypeDef() {
    return findNotNullChildByClass(PlankTypeDef.class);
  }

}
