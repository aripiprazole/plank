package com.lorenzoog.jplank.intellijplugin.analyzer

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile

interface CodeAnnotation {
  val node: PsiElement

  fun bind(file: PlankPsiFile, holder: AnnotationHolder)
}
