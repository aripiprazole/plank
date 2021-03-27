// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankDecl extends PsiElement {

  @Nullable
  PlankClassDecl getClassDecl();

  @Nullable
  PlankFunDecl getFunDecl();

  @Nullable
  PlankLetDecl getLetDecl();

  @Nullable
  PlankNativeFunDecl getNativeFunDecl();

}
