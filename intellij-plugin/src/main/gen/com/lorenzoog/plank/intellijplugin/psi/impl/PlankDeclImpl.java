// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.lorenzoog.plank.intellijplugin.psi.*;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;

public class PlankDeclImpl extends ASTWrapperPsiElement implements PlankDecl {

  public PlankDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankClassDecl getClassDecl() {
    return findChildByClass(PlankClassDecl.class);
  }

  @Override
  @Nullable
  public PlankFunDecl getFunDecl() {
    return findChildByClass(PlankFunDecl.class);
  }

  @Override
  @Nullable
  public PlankLetDecl getLetDecl() {
    return findChildByClass(PlankLetDecl.class);
  }

  @Override
  @Nullable
  public PlankNativeFunDecl getNativeFunDecl() {
    return findChildByClass(PlankNativeFunDecl.class);
  }

}
