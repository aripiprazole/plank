// This is a generated file. Not intended for manual editing.
package com.lorenzoog.jplank.intellijplugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PlankStmt extends PsiElement {

  @Nullable
  PlankDecl getDecl();

  @Nullable
  PlankExprStmt getExprStmt();

  @Nullable
  PlankIfExpr getIfExpr();

  @Nullable
  PlankReturnStmt getReturnStmt();

}
