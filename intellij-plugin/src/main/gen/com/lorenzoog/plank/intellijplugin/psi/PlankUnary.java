// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankUnary extends PsiElement {

  @Nullable
  PlankCallExpr getCallExpr();

  @Nullable
  PlankUnary getUnary();

  @Nullable String getOperator();

}
