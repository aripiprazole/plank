package com.lorenzoog.jplank.intellijplugin.analyzer

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile

data class TypeViolation(
  val expected: Any,
  val actual: PlankType,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder
      .newAnnotation(HighlightSeverity.ERROR, "Expected $expected but got $actual")
      .range(node.textRange)
      .create()
  }
}

data class AssignImmutableViolation(
  val name: String,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder
      .newAnnotation(HighlightSeverity.ERROR, "Variable $name is not mutable")
      .range(node.textRange)
      .create()
  }
}

data class UnexpectedGenericArgument(
  val expected: Int,
  val actual: Int,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    if (expected == 0) {
      return holder
        .newAnnotation(HighlightSeverity.ERROR, "Unexpected generic arguments")
        .range(node.textRange)
        .create()
    }

    holder
      .newAnnotation(
        HighlightSeverity.ERROR,
        "Unexpected $actual generic arguments, expected $expected"
      )
      .range(node.textRange)
      .create()
  }
}

data class UnresolvedTypeViolation(
  val type: String,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder
      .newAnnotation(HighlightSeverity.ERROR, "Unresolved type $type")
      .range(node.textRange)
      .create()
  }
}

data class UnresolvedVariableViolation(
  val name: String,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder
      .newAnnotation(HighlightSeverity.ERROR, "Unresolved variable $name")
      .range(node.textRange)
      .create()
  }
}

data class UnresolvedModuleViolation(
  val name: String,
  override val node: PsiElement,
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder
      .newAnnotation(HighlightSeverity.ERROR, "Unresolved module $name")
      .range(node.textRange)
      .create()
  }
}
