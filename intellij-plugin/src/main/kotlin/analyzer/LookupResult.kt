package com.lorenzoog.jplank.intellijplugin.analyzer

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.lorenzoog.jplank.analyzer.type.PlankType
import javax.swing.Icon

data class LookupResult(
  val kind: Kind,
  val name: String,
  val type: PlankType,
  val extraParams: String = ""
) {
  enum class Kind(val icon: Icon) {
    Function(PlatformIcons.FUNCTION_ICON),
    Struct(PlatformIcons.CLASS_ICON),
    Enum(PlatformIcons.ABSTRACT_CLASS_ICON),
    Variable(PlatformIcons.VARIABLE_ICON),
    Method(PlatformIcons.METHOD_ICON);
  }

  fun toLookupElement(): LookupElement {
    return LookupElementBuilder.create(name)
      .withTailText("($extraParams)")
      .withTypeText(type.toString())
      .withIcon(kind.icon)
  }
}
