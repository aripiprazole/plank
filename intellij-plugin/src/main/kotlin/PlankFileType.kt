package com.lorenzoog.jplank.intellijplugin

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object PlankFileType : LanguageFileType(Plank) {
  private const val name = "Plank"
  private const val description = "Plank programming language file"
  private const val defaultExtension = "plank"

  override fun getName(): String {
    return name
  }

  override fun getDescription(): String {
    return description
  }

  override fun getDefaultExtension(): String {
    return defaultExtension
  }

  override fun getIcon(): Icon? {
    return null
  }
}
