// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import java.util.List;

import com.lorenzoog.jplank.intellijplugin.psi.PsiScope;
import org.jetbrains.annotations.*;
import com.lorenzoog.jplank.analyzer.Variable;
import com.lorenzoog.jplank.analyzer.type.PlankType;
import com.lorenzoog.jplank.analyzer.type.PlankType.Callable;
import com.lorenzoog.jplank.analyzer.type.PlankType.Struct;
import com.lorenzoog.jplank.intellijplugin.analyzer.LookupResult;
import com.lorenzoog.jplank.intellijplugin.analyzer.Scope;

public interface PlankThenBranch extends PsiScope {

  @NotNull
  List<PlankStmt> getStmtList();

  void declare(@NotNull String name, @NotNull PlankType type, boolean mutable);

  void define(@NotNull String name, @NotNull Struct type);

  @NotNull
  Scope expand(@NotNull Scope another);

  @Nullable
  Callable findFunction(@NotNull String name);

  @Nullable
  Struct findStruct(@NotNull String name);

  @Nullable
  PlankType findType(@NotNull String name);

  @Nullable
  Variable findVariable(@NotNull String name);

  @NotNull
  List<LookupResult> lookup();

}
