package com.lorenzoog.jplank.intellijplugin

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType

class Plank : Language("plank") {
  override fun getAssociatedFileType(): LanguageFileType {
    return PlankFileType.INSTANCE
  }

  companion object {
    val INSTANCE = Plank()
  }
}
