// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankLetDecl extends PsiElement {

  @NotNull
  PlankExpr getExpr();

  @Nullable
  PlankTypeDef getTypeDef();

  @NotNull String getName();

  boolean getMutable();

}
