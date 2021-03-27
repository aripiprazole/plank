// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;

public interface PlankEquality extends PsiElement {

  @NotNull
  List<PlankComparison> getComparisonList();

  @NotNull PlankComparison getLhs();

  @NotNull Map<String, PlankComparison> getRightmostOperands();

}
