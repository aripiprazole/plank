package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.lorenzoog.jplank.intellijplugin.Plank
import com.lorenzoog.jplank.intellijplugin.PlankFileType

class PlankFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, Plank.INSTANCE) {
  override fun getFileType(): FileType {
    return PlankFileType.INSTANCE
  }
}
