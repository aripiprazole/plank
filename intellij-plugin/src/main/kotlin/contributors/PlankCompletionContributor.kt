package com.lorenzoog.jplank.intellijplugin.contributors

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import com.lorenzoog.jplank.intellijplugin.Plank
import com.lorenzoog.jplank.intellijplugin.psi.PlankTypes

class PlankCompletionContributor : CompletionContributor() {
  init {
    extend(
      CompletionType.BASIC,
      PlatformPatterns.psiElement(PlankTypes.WS).withLanguage(Plank.INSTANCE),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
          parameters: CompletionParameters,
          context: ProcessingContext,
          result: CompletionResultSet
        ) {
          result.addElement(
            LookupElementBuilder.create("println")
              .withTypeText("(io)")
              .withTailText("Void")
              .withIcon(PlatformIcons.FUNCTION_ICON)
          )
        }
      }
    )
  }
}
