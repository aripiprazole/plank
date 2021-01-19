// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;

public interface PlankComparison extends PsiElement {

  @NotNull
  List<PlankTerm> getTermList();

  @NotNull PlankTerm getLhs();

  @NotNull Map<String, PlankTerm> getRightmostOperands();

}
