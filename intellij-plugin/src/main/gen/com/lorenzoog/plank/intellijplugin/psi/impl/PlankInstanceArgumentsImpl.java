// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import java.util.List;

import com.lorenzoog.plank.intellijplugin.psi.PlankArgument;
import com.lorenzoog.plank.intellijplugin.psi.PlankExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankInstanceArguments;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import java.util.Map;

public class PlankInstanceArgumentsImpl extends ASTWrapperPsiElement implements PlankInstanceArguments {

  public PlankInstanceArgumentsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitInstanceArguments(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankArgument> getArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankArgument.class);
  }

  @Override
  public @NotNull Map<String, PlankExpr> getArguments() {
    return PlankPsiImplUtil.getArguments(this);
  }

}
