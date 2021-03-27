// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;

public interface PlankTerm extends PsiElement {

  @NotNull
  List<PlankFactor> getFactorList();

  @NotNull PlankFactor getLhs();

  @NotNull Map<String, PlankFactor> getRightmostOperands();

}
