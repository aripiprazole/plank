package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.psi.tree.IElementType
import com.lorenzoog.jplank.intellijplugin.Plank

class PlankTokenType(private val name: String) : IElementType(name, Plank) {
  override fun toString(): String {
    return "PlankTokenType.$name"
  }
}
