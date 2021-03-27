// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import com.lorenzoog.jplank.intellijplugin.psi.PlankDotQualifiedExpr;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankSet extends PsiElement {

  @NotNull
  PlankAssignExpr getAssignExpr();

  @Nullable
  PlankCallExpr getCallExpr();

  @NotNull PlankDotQualifiedExpr getReceiver();

  @NotNull PsiElement getValue();

}
