// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import com.lorenzoog.jplank.intellijplugin.psi.PlankNamedElement;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankAssign extends PsiElement {

  @NotNull
  PlankAssignExpr getAssignExpr();

  @NotNull PlankNamedElement getReceiver();

  @NotNull PsiElement getValue();

}
