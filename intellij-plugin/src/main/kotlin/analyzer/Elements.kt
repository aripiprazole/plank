package com.lorenzoog.jplank.intellijplugin.analyzer

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.intellijplugin.PlankLanguageHighlighter
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile

data class FunctionCallElement(override val node: PsiElement) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .textAttributes(PlankLanguageHighlighter.FUNCTION_NAME)
      .range(node.textRange)
      .create()
  }
}

data class FunctionElement(
  val name: PsiElement,
  override val node: PsiElement
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .textAttributes(PlankLanguageHighlighter.FUNCTION_NAME)
      .range(name.textRange)
      .create()
  }
}

data class StructElement(
  val name: PsiElement,
  override val node: PsiElement
) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .textAttributes(PlankLanguageHighlighter.STRUCT_NAME)
      .range(name.textRange)
      .create()
  }
}

data class TypeReferenceElement(override val node: PsiElement) : CodeAnnotation {
  override fun bind(file: PlankPsiFile, holder: AnnotationHolder) {
    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .textAttributes(PlankLanguageHighlighter.TYPE_REF)
      .range(node.textRange)
      .create()
  }
}
