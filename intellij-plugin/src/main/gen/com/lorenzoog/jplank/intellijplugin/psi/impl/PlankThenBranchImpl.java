// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lorenzoog.jplank.intellijplugin.psi.PlankTypes.*;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import com.lorenzoog.jplank.analyzer.Variable;
import com.lorenzoog.jplank.analyzer.type.PlankType;
import com.lorenzoog.jplank.analyzer.type.PlankType.Callable;
import com.lorenzoog.jplank.analyzer.type.PlankType.Struct;
import com.lorenzoog.jplank.intellijplugin.analyzer.LookupResult;
import com.lorenzoog.jplank.intellijplugin.analyzer.Scope;

public class PlankThenBranchImpl extends PlankDelegateScopedImpl implements PlankThenBranch {

  public PlankThenBranchImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PlankVisitor visitor) {
    visitor.visitThenBranch(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PlankVisitor) accept((PlankVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PlankStmt> getStmtList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PlankStmt.class);
  }

}
