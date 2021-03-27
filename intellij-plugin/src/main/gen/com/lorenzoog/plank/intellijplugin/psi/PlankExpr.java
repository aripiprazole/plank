// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankExpr extends PsiElement {

  @Nullable
  PlankAssignExpr getAssignExpr();

  @Nullable
  PlankIfExpr getIfExpr();

  @Nullable
  PlankSizeofExpr getSizeofExpr();

}
