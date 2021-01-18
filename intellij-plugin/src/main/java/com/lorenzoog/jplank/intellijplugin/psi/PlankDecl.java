// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
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
