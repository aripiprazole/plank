// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankTypeDef extends PsiElement {

  @Nullable
  PlankArrayTypeDef getArrayTypeDef();

  @Nullable
  PlankCallableTypeDef getCallableTypeDef();

  @Nullable
  PlankGenericAccessTypeDef getGenericAccessTypeDef();

  @Nullable
  PlankGenericTypeDef getGenericTypeDef();

  @Nullable
  PlankNameTypeDef getNameTypeDef();

  @Nullable
  PlankPointerTypeDef getPointerTypeDef();

}
