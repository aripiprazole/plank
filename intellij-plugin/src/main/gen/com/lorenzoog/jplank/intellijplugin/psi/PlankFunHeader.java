// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankFunHeader extends PlankNamedElement {

  @NotNull
  List<PlankParameter> getParameterList();

  @NotNull
  PlankTypeDef getTypeDef();

  @NotNull String getName();

  @NotNull PsiElement setName(String newName);

}
