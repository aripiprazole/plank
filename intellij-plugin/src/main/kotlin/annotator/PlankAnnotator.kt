package com.lorenzoog.jplank.intellijplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.lorenzoog.jplank.intellijplugin.analyzer.CodeAnnotation
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile

class PlankAnnotator : ExternalAnnotator<PlankPsiFile, List<CodeAnnotation>>() {
  override fun collectInformation(file: PsiFile): PlankPsiFile {
    return (file as PlankPsiFile).update()
  }

  override fun collectInformation(
    file: PsiFile,
    editor: Editor,
    hasErrors: Boolean
  ): PlankPsiFile {
    return collectInformation(file)
  }

  override fun doAnnotate(file: PlankPsiFile): List<CodeAnnotation> {
    return file.update().analyze()
  }

  override fun apply(
    file: PsiFile,
    bindings: List<CodeAnnotation>,
    holder: AnnotationHolder
  ) {
    bindings.forEach {
      runCatching {
        it.bind(file as PlankPsiFile, holder)
      }
    }
  }
}
