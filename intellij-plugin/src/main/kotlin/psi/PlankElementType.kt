package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.psi.tree.IElementType
import com.lorenzoog.jplank.intellijplugin.Plank

class PlankElementType(private val name: String) : IElementType(name, Plank.INSTANCE) {
  override fun toString(): String {
    return "PlankElementType.$name"
  }
}
