package com.lorenzoog.jplank.intellijplugin

import com.intellij.openapi.editor.colors.TextAttributesKey
import kotlin.properties.ReadOnlyProperty

fun attributesKey(attribute: TextAttributesKey): ReadOnlyProperty<Any, TextAttributesKey> {
  var value: TextAttributesKey? = null

  return ReadOnlyProperty { _, property ->
    if (value == null) {
      value = TextAttributesKey.createTextAttributesKey(
        "PLANK_${property.name.toUpperCase()}",
        attribute
      )
    }

    value!!
  }
}
