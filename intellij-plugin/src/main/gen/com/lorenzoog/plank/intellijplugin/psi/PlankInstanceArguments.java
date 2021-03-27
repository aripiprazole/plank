// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;

public interface PlankInstanceArguments extends PsiElement {

  @NotNull
  List<PlankArgument> getArgumentList();

  @NotNull Map<String, PlankExpr> getArguments();

}
