// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.jplank.intellijplugin.psi.impl.PlankNamedElementImpl;
import com.lorenzoog.plank.intellijplugin.psi.PlankNameTypeDef;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankNameTypeDefImpl extends PlankNamedElementImpl implements PlankNameTypeDef {

  public PlankNameTypeDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitNameTypeDef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

}
