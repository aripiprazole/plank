package com.lorenzoog.jplank.intellijplugin

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType

object Plank : Language("plank") {
  override fun getAssociatedFileType(): LanguageFileType {
    return PlankFileType
  }
}
