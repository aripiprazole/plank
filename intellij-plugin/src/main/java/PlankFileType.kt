package com.lorenzoog.jplank.intellijplugin

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class PlankFileType : LanguageFileType(Plank.INSTANCE) {
  private val name = "Plank"
  private val description = "Plank programming language file"
  private val defaultExtension = "plank"

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

  companion object {
    val INSTANCE = PlankFileType()
  }
}
