// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import java.util.List;

import com.lorenzoog.jplank.intellijplugin.psi.PlankNamedElement;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.Map;
import kotlin.Pair;

public interface PlankClassDecl extends PlankNamedElement {

  @NotNull
  List<PlankField> getFieldList();

  @NotNull String getName();

  @NotNull PsiElement setName(String newName);

  @NotNull Map<String, Pair<Boolean, PlankTypeDef>> getFields();

}
