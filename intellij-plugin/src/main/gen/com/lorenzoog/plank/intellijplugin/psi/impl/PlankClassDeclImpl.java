// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import java.util.List;

import com.lorenzoog.plank.intellijplugin.psi.PlankClassDecl;
import com.lorenzoog.plank.intellijplugin.psi.PlankField;
import com.lorenzoog.plank.intellijplugin.psi.PlankTypeDef;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import java.util.Map;
import kotlin.Pair;

public class PlankClassDeclImpl extends ASTWrapperPsiElement implements PlankClassDecl {

  public PlankClassDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitClassDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankField> getFieldList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankField.class);
  }

  @Override
  public @NotNull String getName() {
    return PlankPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull PsiElement setName(String newName) {
    return PlankPsiImplUtil.setName(this, newName);
  }

  @Override
  public @NotNull Map<String, Pair<Boolean, PlankTypeDef>> getFields() {
    return PlankPsiImplUtil.getFields(this);
  }

}
