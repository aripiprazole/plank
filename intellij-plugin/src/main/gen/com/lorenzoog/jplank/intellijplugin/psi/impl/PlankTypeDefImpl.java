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

public class PlankTypeDefImpl extends ASTWrapperPsiElement implements PlankTypeDef {

  public PlankTypeDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitTypeDef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankArrayTypeDef getArrayTypeDef() {
    return findChildByClass(PlankArrayTypeDef.class);
  }

  @Override
  @Nullable
  public PlankCallableTypeDef getCallableTypeDef() {
    return findChildByClass(PlankCallableTypeDef.class);
  }

  @Override
  @Nullable
  public PlankGenericAccessTypeDef getGenericAccessTypeDef() {
    return findChildByClass(PlankGenericAccessTypeDef.class);
  }

  @Override
  @Nullable
  public PlankGenericTypeDef getGenericTypeDef() {
    return findChildByClass(PlankGenericTypeDef.class);
  }

  @Override
  @Nullable
  public PlankNameTypeDef getNameTypeDef() {
    return findChildByClass(PlankNameTypeDef.class);
  }

  @Override
  @Nullable
  public PlankPointerTypeDef getPointerTypeDef() {
    return findChildByClass(PlankPointerTypeDef.class);
  }

}
