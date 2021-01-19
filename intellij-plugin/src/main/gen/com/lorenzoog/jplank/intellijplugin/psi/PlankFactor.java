// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;

public interface PlankFactor extends PsiElement {

  @NotNull
  List<PlankUnary> getUnaryList();

  @NotNull PlankUnary getLhs();

  @NotNull Map<String, PlankUnary> getRightmostOperands();

}
