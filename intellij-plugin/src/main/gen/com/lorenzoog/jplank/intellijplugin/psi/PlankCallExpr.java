// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankCallExpr extends PsiElement {

  @NotNull
  List<PlankArguments> getArgumentsList();

  @NotNull
  List<PlankGet> getGetList();

  @Nullable
  PlankInstanceArguments getInstanceArguments();

  @NotNull
  PlankPointer getPointer();

}
