package com.lorenzoog.plank.intellijplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.lorenzoog.jplank.intellijplugin.psi.*;
import com.lorenzoog.jplank.intellijplugin.psi.impl.PlankNamedElementImpl;
import com.lorenzoog.plank.intellijplugin.psi.*;
import kotlin.NotImplementedError;
import kotlin.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PlankPsiImplUtil {
  @Contract(pure = true)
  @NotNull
  public static PlankDotQualifiedExpr getReceiver(PlankSet set) {
    return new PlankDotQualifiedExpr(Objects.requireNonNull(set.getCallExpr()), findChild(PlankTypes.IDENTIFIER,set));
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getValue(@NotNull PlankSet set) {
    return set.getAssignExpr();
  }

  @Contract(pure = true)
  @NotNull
  public static PlankNamedElement getReceiver(PlankAssign assign) {
    return new PlankNamedElementImpl(findChild(PlankTypes.IDENTIFIER, assign));
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getValue(@NotNull PlankAssign assign) {
    return assign.getAssignExpr();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getValue(@NotNull PlankAssignExpr assignExpr) {
    final PlankAssign assign = assignExpr.getAssign();
    if(assign != null) {
      return assign.getValue();
    }

    final PlankSet set = assignExpr.getSet();
    if(set != null) {
      return set.getValue();
    }

    throw new IllegalStateException("The code should never reach here");
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getReceiver(@NotNull PlankGenericTypeDef generic) {
    return new PlankNamedElementImpl(findChild(PlankTypes.IDENTIFIER, generic));
  }

  @Contract(pure = true)
  public static boolean getMutable(@NotNull PlankField field) {
    return findChildOrNull(PlankTypes.MUTABLE, field) != null;
  }

  @Contract(pure = true)
  public static boolean getMutable(@NotNull PlankLetDecl letDecl) {
    return findChildOrNull(PlankTypes.MUTABLE, letDecl) != null;
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(@NotNull PlankLetDecl letDecl) {
    return findChild(PlankTypes.IDENTIFIER, letDecl).getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(PlankFunHeader funHeader, String newName) {
    throw new NotImplementedError("TODO");
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(@NotNull PlankFunHeader funHeader) {
    return findChild(PlankTypes.IDENTIFIER, funHeader).getText();
  }

  @Contract(pure = true)
  @Nullable
  public static String getOperator(@NotNull PlankUnary unary) {
    final PlankUnary rhs = unary.getUnary();
    if(rhs == null) {
      return null;
    }

    return rhs.getPrevSibling().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PlankUnary getLhs(@NotNull PlankFactor factor) {
    return (PlankUnary) factor.getFirstChild();
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, PlankUnary> getRightmostOperands(PlankFactor factor) {
    return new HashMap<>();
  }

  @Contract(pure = true)
  @NotNull
  public static PlankFactor getLhs(@NotNull PlankTerm term) {
    return (PlankFactor) term.getFirstChild();
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, PlankFactor> getRightmostOperands(PlankTerm equality) {
    return new HashMap<>();
  }

  @Contract(pure = true)
  @NotNull
  public static PlankTerm getLhs(@NotNull PlankComparison comparison) {
    return (PlankTerm) comparison.getFirstChild();
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, PlankTerm> getRightmostOperands(PlankComparison equality) {
    return new HashMap<>();
  }

  @Contract(pure = true)
  @NotNull
  public static PlankComparison getLhs(@NotNull PlankEquality equality) {
    return (PlankComparison) equality.getFirstChild();
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, PlankComparison> getRightmostOperands(PlankEquality equality) {
    return new HashMap<>();
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, Pair<Boolean, PlankTypeDef>> getFields(@NotNull PlankClassDecl classDecl) {
    final HashMap<String, Pair<Boolean, PlankTypeDef>> arguments = new HashMap<>();

    for (PlankField field : classDecl.getFieldList()) {
      final PlankParameter parameter = field.getParameter();

      arguments.put(
        parameter.getName(),
        new Pair<>(
          field.getMutable(),
          parameter.getTypeDef()
        )
      );
    }

    return arguments;
  }

  @Contract(pure = true)
  @NotNull
  public static Map<String, PlankExpr> getArguments(@NotNull PlankInstanceArguments instance) {
    final HashMap<String, PlankExpr> arguments = new HashMap<>();

    for (PlankArgument argument : instance.getArgumentList()) {
      final String parameterName = findChild(PlankTypes.IDENTIFIER, argument).getText();
      final PlankExpr expr = argument.getExpr();

      arguments.put(parameterName, expr);
    }

    return arguments;
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(@NotNull PlankClassDecl classDecl) {
    return findChild(PlankTypes.IDENTIFIER, classDecl).getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(PlankClassDecl classDecl, String newName) {
    throw new NotImplementedError("TODO");
  }

  @Contract(pure = true)
  @Nullable
  public static ASTNode findChildOrNull(IElementType type, @NotNull PsiElement element) {
    PsiElement child = element.getFirstChild();

    while (child != null) {
      if(child.getNode().getElementType().equals(type)) {
        return child.getNode();
      }
      child = child.getNextSibling();
    }

    return null;
  }

  @Contract(pure = true)
  @NotNull
  public static ASTNode findChild(IElementType type, @NotNull PsiElement element) {
    PsiElement child = element.getFirstChild();

    while (child != null) {
      if(child.getNode().getElementType().equals(type)) {
        return child.getNode();
      }
      child = child.getNextSibling();
    }

    throw new IllegalStateException("The code should never reach here");
  }
}
