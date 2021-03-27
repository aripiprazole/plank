// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi.impl;

import java.util.List;

import com.lorenzoog.jplank.intellijplugin.psi.impl.PlankDelegateScopedImpl;
import com.lorenzoog.plank.intellijplugin.psi.PlankElseBranch;
import com.lorenzoog.plank.intellijplugin.psi.PlankExpr;
import com.lorenzoog.plank.intellijplugin.psi.PlankStmt;
import com.lorenzoog.plank.intellijplugin.psi.PlankVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import com.lorenzoog.jplank.analyzer.type.PlankType;
import com.lorenzoog.jplank.analyzer.type.PlankType.Callable;
import com.lorenzoog.jplank.analyzer.type.PlankType.Struct;

public class PlankElseBranchImpl extends PlankDelegateScopedImpl implements PlankElseBranch {

  public PlankElseBranchImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitElseBranch(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PlankExpr getExpr() {
    return findChildByClass(PlankExpr.class);
  }

  @Override
  @NotNull
  public List<PlankStmt> getStmtList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankStmt.class);
  }

}
